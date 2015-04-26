package com.danielmmy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import com.danielmmy.Task.TaskType;
/**
 * Affinity aware scheduler capable of allocating a maximum of three parallel tasks.
 */
public class AffinityAwareScheduler extends Scheduler {
	protected int MAX_TASK_AGE=15;
	protected List<Node> mMatches;
	protected List<Double> mAffinities;


	public AffinityAwareScheduler(List<Task> tasks, List<Integer> arrivalOrder,	List<Node> infrastructure, double[][][] AFFINITY3, AffinityType affinityType, int maxAge){
		super(tasks, arrivalOrder, infrastructure, AffinityAwareScheduler.class.getSimpleName(), AFFINITY3, affinityType);
		// TODO Auto-generated constructor stub
		this.MAX_TASK_AGE=maxAge;
	}
	
	/*
	 * For extended class use. Pass the child class name as parameter
	 */
	protected AffinityAwareScheduler(List<Task> tasks, List<Integer> arrivalOrder,	List<Node> infrastructure, String outputFilename, double[][][] AFFINITY3, AffinityType affinityType, int maxAge ){
		super(tasks, arrivalOrder, infrastructure, outputFilename, AFFINITY3, affinityType);
		// TODO Auto-generated constructor stub
		this.MAX_TASK_AGE=maxAge;
	}

	@Override
	protected void allocateTask() {
		Iterator<Task> iterator=mWaitingQueue.iterator();
		Task t;
		while (iterator.hasNext()) {
			t=iterator.next();
			if(tryToAllocate(t)){
				iterator.remove();
				mRunningQueue.add(t);
			}
		}
	}

	protected boolean tryToAllocate(Task task){
		List<Node> freeNodes=new ArrayList<>();//stores available nodes for allocation
		List<TaskInstance> instances=task.getmInstances();//Store the instances that need to be allocated
		int totalInstances=instances.size();//Number of instances to allocate
		int foundAvailable=0;//Number of free instances slots found

		int coresPerInstance=instances.get(0).getmConsumedCores();//How many cores does a single instance need
		int ramPerInstance=instances.get(0).getmConsumedRam();//How much RAM does a single instance need
		for(Node n:mInfrastructure){//Loop through available nodes;
			int nodeFreeInstanceCores=n.getFreeCores()/coresPerInstance;//How many instances can be allocated based on cores, no super-allocation is permitted
			int nodeFreeInstanceRam=n.getFreeRam()/ramPerInstance;//How many instances can be allocated based on RAM, no super-allocation is permitted
			if(nodeFreeInstanceCores>0&&nodeFreeInstanceRam>0&&n.canAddInstance()>0){//If there is slot for at least one instance
				freeNodes.add(n);//Add to available nodes
				foundAvailable+=min(nodeFreeInstanceCores,nodeFreeInstanceRam,n.canAddInstance());//Increase found free nodes counter by how many slots are available
			}

		}
		if(foundAvailable>=totalInstances){//There are resources
			double cutPoint;//minimum acceptable affinity
			mAffinities=new ArrayList<>();
			mMatches=new ArrayList<>();

			if(task.getmAge()>=MAX_TASK_AGE){//Exceeded maximum age
				cutPoint=0;
				//System.out.println("maximum exceeded");
			}else{
				cutPoint=0.5;
			}

			for(Node freeNode:freeNodes){
				double affinity=freeNode.getAffinityWithTask(task.getmTaskType(), AFFINITY3);
				if(affinity>cutPoint){//can add at least one instance
					if(mAffinities.size()==totalInstances){//already found enough mMatches
						insertIfBetterMatch(freeNode,affinity);
					}else{//insert match
						mMatches.add(freeNode);
						mAffinities.add(affinity);
					}
					int nodeFreeInstanceCores=freeNode.getFreeCores()/coresPerInstance;//How many instances can be allocated based on cores, no super-allocation is permitted
					int nodeFreeInstanceRam=freeNode.getFreeRam()/ramPerInstance;//How many instances can be allocated based on RAM, no super-allocation is permitted
					if(min(nodeFreeInstanceCores,nodeFreeInstanceRam,freeNode.canAddInstance())>1){//can possibly add at least two instances
						affinity=freeNode.getAffinityWithTasks(task.getmTaskType(),task.getmTaskType(), AFFINITY3);
						if(affinity>cutPoint){//can add another instance
							if(mAffinities.size()==totalInstances){//already found enough mMatches
								insertIfBetterMatch(freeNode,affinity);
							}else{//insert match
								mMatches.add(freeNode);
								mAffinities.add(affinity);
							}
							nodeFreeInstanceCores=freeNode.getFreeCores()/coresPerInstance;//How many instances can be allocated based on cores, no super-allocation is permitted
							nodeFreeInstanceRam=freeNode.getFreeRam()/ramPerInstance;//How many instances can be allocated based on RAM, no super-allocation is permitted
							if(min(nodeFreeInstanceCores,nodeFreeInstanceRam,freeNode.canAddInstance())>2){//can possibly add at least three instances
								affinity=freeNode.getAffinityWithTasks(task.getmTaskType(),task.getmTaskType(),task.getmTaskType(), AFFINITY3);
								if(affinity>cutPoint){//can add another instance
									if(mAffinities.size()==totalInstances){//already found enough mMatches
										insertIfBetterMatch(freeNode,affinity);
									}else{//insert match
										mMatches.add(freeNode);
										mAffinities.add(affinity);
									}
								}
							}
						}
					}
				}
			}
			if(mAffinities.size()==totalInstances){//will allocate
				//Allocating instances
				for(Node freeNode: mMatches){
					try {
						freeNode.addInstance(instances.remove(0));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return true;
			}else{//will not allocate, but there are resources available. Incrementing age
				task.incrementAge();
				return false;
			}
		}else{//can not allocate
			return false;
		}
	}

	private void insertIfBetterMatch(Node node, double affinity){
		ListIterator<Node> matchesIterator=mMatches.listIterator();
		ListIterator<Double> affinitiesIterator = mAffinities.listIterator();
		while(matchesIterator.hasNext()){
			double previousAffinity=affinitiesIterator.next();//receive current iterator affinity
			Node previousMatch=matchesIterator.next();//receive current iterator match 
			if(affinity>previousAffinity){//if the affinity is greater then the previous affinity exchange
				matchesIterator.remove();//removes smaller affinity node
				affinitiesIterator.remove();//removes smaller affinity
				matchesIterator.add(node);//adds the node before the iterator
				affinitiesIterator.add(affinity);//adds the affinity before the iterator
				//System.out.print("Out:"+ previousAffinity+ " In:"+affinity);
				node=previousMatch;//Save the node to compare to the rest of the list
				affinity=previousAffinity;//Save the affinity to compare to the rest of the list
				
				
			}
				
		}
		//System.out.println();
	}
	
	
	/*
	 * save results to file
	 */
	@Override
	protected void saveResults(){
		DateFormat dateFormat = new SimpleDateFormat("_yyyy_MM_dd_HH:mm:ss");
		Date date = new Date();
		try(BufferedWriter bw=new BufferedWriter(new FileWriter("/home/daniel/results/"+this.mAffinityType+"/"+this.getClass().getSimpleName()+"/"+MAX_TASK_AGE+"/"+mOutputFileName+dateFormat.format(date)))){
			bw.write(this.getClass().getSimpleName()+" with max age="+MAX_TASK_AGE+"\n");
			bw.write("Total processing time: "+cycles+" TU\n");
			int count=0;
			for(Task t : mTasks){
				bw.write(++count + ":");
				if(t.getmAge()>=MAX_TASK_AGE){
					bw.write("Aging used\n");
				}else{
					bw.write("Aging not used\n");
				}
			}
			bw.write("###############################################################\n");
			for(int i=0;i<Task.TYPES_OF_TASK_NUMBER;++i){
				for(int j=0;j<Task.TYPES_OF_TASK_NUMBER;++j){
					for(int k=0;k<Task.TYPES_OF_TASK_NUMBER;++k){
						 DecimalFormat df = new DecimalFormat("0.0");
						bw.write("["+df.format(AFFINITY3[i][j][k])+"]");
					}
					bw.write("\n");
				}
				bw.write("###############################################################\n");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * test case varying age
	 */
	public static void main(String[] args){
		int increment=1;
		for(int m=0;m<=5000;m+=increment){
			if(m==10)
				increment=5;
			if(m==100)
				increment=100;
			if(m==500)
				increment=500;

			for(int l=0;l<30;++l){
				double[][][] AFFINITY3=new double[Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER];
				Random r=new Random(l);
				double affinity;
				for(int i=0;i<Task.TYPES_OF_TASK_NUMBER;++i){
					for(int j=0;j<Task.TYPES_OF_TASK_NUMBER;++j){
						for(int k=0;k<Task.TYPES_OF_TASK_NUMBER;++k){
							do{
								affinity=r.nextDouble();
							}while(affinity==0);
							AFFINITY3[i][j][k]=AFFINITY3[i][k][j]=AFFINITY3[k][i][j]=AFFINITY3[j][i][k]=AFFINITY3[k][j][i]=AFFINITY3[j][k][i]=affinity;	
						}
					}
				}
				List<Node> infrastructure=new ArrayList<>();
				for(int i=0; i<TEST_NUMBER_OF_NODES;++i)
					infrastructure.add(new Node(TEST_NUMBER_OF_CORES, TEST_RAM_SIZE));
				List<Task> tasks=new ArrayList<>();
				TaskType[] types=TaskType.values();
				for(int i=0;i<TEST_NUMBER_OF_PROCESS;++i){
					TaskType type=types[r.nextInt(Task.TYPES_OF_TASK_NUMBER)];
					int instanceNumber=r.nextInt(101)+10;//from 10 to 100 instances
					int coresPerInstance=r.nextInt(12)+1;//from 1 to 12
					int ramPerinstance=r.nextInt(12*1024+101)+100;//from 100MB to 12GB
					double baseTime=r.nextInt(10001)+1000;//from 100 cycles to 1000
					tasks.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				}
				List<Integer> arrivalOrder=new ArrayList<>();
				int tasksSoFar=0;
				do{
					tasksSoFar+=r.nextInt(11);
					arrivalOrder.add((tasksSoFar<tasks.size())?tasksSoFar:tasks.size());
				}while(tasksSoFar<tasks.size());
				final Scheduler scheduler=new AffinityAwareScheduler(tasks, arrivalOrder, infrastructure,AFFINITY3,AffinityType.NORMAL,m);
				Thread t=new Thread(new Runnable() {		
					@Override
					public void run() {
						scheduler.runScheduler();
					}
				});
				t.start();
				try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Finished running.");
			}
		}
	}
}
