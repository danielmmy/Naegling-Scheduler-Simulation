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
import java.util.Random;

import com.danielmmy.Task.TaskType;

public class FirstAvailableScheduler extends Scheduler{
	
	public FirstAvailableScheduler(List<Task> tasks, List<Integer> arrivalOrder, List<Node> infrastructure, double[][][] AFFINITY3, AffinityType affinityType ){
		super(tasks, arrivalOrder, infrastructure, FirstAvailableScheduler.class.getSimpleName(), AFFINITY3, affinityType);	
	}
	
	/*
	 * For extended class use. Pass the child class name as parameter
	 */
	protected FirstAvailableScheduler(List<Task> tasks, List<Integer> arrivalOrder, List<Node> infrastructure,String outputFileName, double[][][] AFFINITY3, AffinityType affinityType ){
		super(tasks, arrivalOrder, infrastructure, outputFileName, AFFINITY3, affinityType);	
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
				if(foundAvailable>=totalInstances)//If found enough slots stop search
					break;
			}

		}
		if(foundAvailable>=totalInstances){//will allocate
			for(Node freeNode: freeNodes){
				while(instances.size()>0&&freeNode.canAddInstance()>0&&freeNode.getFreeCores()>=coresPerInstance&&freeNode.getFreeRam()>=ramPerInstance){
					try {
						freeNode.addInstance(instances.remove(0));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return true;
		}else{//can not allocate
			return false;
		}
	}
	
	/*
	 * save results to file
	 */
	@Override
	protected void saveResults(){
		DateFormat dateFormat = new SimpleDateFormat("_yyyy_MM_dd_HH:mm:ss");
		Date date = new Date();
		try(BufferedWriter bw=new BufferedWriter(new FileWriter("/home/daniel/results/"+this.mAffinityType+"/"+this.getClass().getSimpleName()+"/"+mOutputFileName+dateFormat.format(date)))){
			bw.write(this.getClass().getSimpleName()+"\n");
			bw.write("Total processing time: "+cycles+" TU\n");
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
	 * test case
	 */
	public static void main(String[] args){
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
			final Scheduler scheduler=new FirstAvailableScheduler(tasks, arrivalOrder, infrastructure, AFFINITY3, AffinityType.NORMAL);
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
