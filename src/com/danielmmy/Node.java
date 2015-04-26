package com.danielmmy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.danielmmy.Task.TaskType;



public class Node {
	private int mCores;
	private int mRam;
	private List<TaskInstance> mRunningInstances;
	
	public Node(int cores, int ram){
		mCores=cores;
		mRam=ram;
		mRunningInstances=new ArrayList<>();
	}
	
	/*
	 * get the number of free cores
	 */
	public int getFreeCores(){
		int totalUsed=0;
		for(TaskInstance i: mRunningInstances)
			totalUsed+=i.getmConsumedCores();
		return mCores-totalUsed;
	}
	
	/*
	 * get the node available RAM size in MB
	 */
	public int getFreeRam(){
		int totalUsed=0;
		for(TaskInstance i: mRunningInstances)
			totalUsed+=i.getmConsumedRam();
		return mRam-totalUsed;
	}
	
	/*
	 * Check if the maximum number of parallel tasks has not been exceeded 
	 */
	public int canAddInstance(){
		return Task.MAX_CONCURRENT_TASK_INSTANCES-mRunningInstances.size();
	}
	
	public void addInstance(TaskInstance taskInstance) throws Exception{
		if(canAddInstance()>0)
			mRunningInstances.add(taskInstance);
		else 
			throw new Exception("Maximum number of running instances exceded");
	}
	
	/*
	 * remove a running tasks. Usually called after the task finishes
	 */
	public void removeTask(Task task){
		Iterator<TaskInstance> iterator=mRunningInstances.iterator();
		while(iterator.hasNext()){
			TaskInstance instance=iterator.next();
			if(instance.belongsTo(task)){
				iterator.remove();
			}
		}
	}
	

	private int[] getCurrentProcessTypes(){
		int [] indexes=new int[mRunningInstances.size()];
		int i=0;
		for(TaskInstance instance: mRunningInstances)
			indexes[i++]=instance.getType().ordinal();
		return indexes;
	}
	
	
	
	
	
	/*
	 * process the tasks running within the node
	 */
	public void processTasks(double[][][] AFFINITY3){
		int[] indexes=getCurrentProcessTypes();
		double affinity=1.0;
		if(indexes.length==3)
			affinity=AFFINITY3[indexes[2]][indexes[1]][indexes[0]];
		else if(indexes.length==2)
			affinity=AFFINITY3[0][indexes[1]][indexes[0]];
		for(TaskInstance instance: mRunningInstances){
			instance.addProcessTime(affinity);
		}
	}
	
	/*
	 * return the affinity of type with the running tasks
	 */
	public double getAffinityWithTask(TaskType type, double[][][] AFFINITY3){
		double affinity=1.0;
		if(mRunningInstances.size()==0)//No running tasks return affinity 1
			return affinity;

		int[] indexes=getCurrentProcessTypes();
		if(indexes.length==2)//2 running tasks
			affinity=AFFINITY3[type.ordinal()][indexes[1]][indexes[0]];
		else //1 running task
			affinity=AFFINITY3[0][type.ordinal()][indexes[0]];
		return affinity;
	}
	
	/*
	 * return the affinity of type1 and type2 with the running tasks
	 */
	public double getAffinityWithTasks(TaskType type1, TaskType type2, double[][][] AFFINITY3){
		double affinity=1.0;
		if(mRunningInstances.size()==1){
			int[] indexes=getCurrentProcessTypes();
			affinity=AFFINITY3[type2.ordinal()][type1.ordinal()][indexes[0]];
		}else{
			affinity=AFFINITY3[0][type2.ordinal()][type1.ordinal()];
		}
		
		return affinity;
	}
	
	/*
	 * return the affinity of type1, type2 and type3 with the running tasks. For a maximum of 3 parallel tasks there is no other running task
	 */
	public double getAffinityWithTasks(TaskType type1, TaskType type2, TaskType type3, double[][][] AFFINITY3){
		return AFFINITY3[type3.ordinal()][type2.ordinal()][type1.ordinal()];
	}

}
