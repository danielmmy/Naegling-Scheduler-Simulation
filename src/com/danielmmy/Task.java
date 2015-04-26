package com.danielmmy;


import java.util.ArrayList;
import java.util.List;

public class Task {
	
	public enum TaskType{
		HPL,PARPAC,B_EFF_PROCESS,B_EFF_IO,PRIOMARK_COMMON,PRIOMARK_SINGLE,UNKNOWN
	}
	public static final int TYPES_OF_TASK_NUMBER=TaskType.values().length;
	public static final int MAX_CONCURRENT_TASK_INSTANCES=3;
	private static int ID=0;
	
	
	//Task ID
	private int mTaskID;
	private TaskType mTaskType;
	//Number of nodes needed by task
	private double mBaseTime;
	//Time processing after applying affinity factor
	private double mProcessTimeElapsed;
	//Real time processing
	private int mRealTimeElapsed;
	//Instances to process task
	private List<TaskInstance> mInstances;
	//Number of core consumed by each instance
	private int mCoresPerInstance;
	//RAM size consumed by each instance
	private int mRamPerInstance;
	//Time for a task to wait for resources
	private int mAge;
	
	
	public Task(TaskType taskType,int instanceNumbers, int coresPerinstance, int ramPerInstance, double baseTime){
		mTaskID=ID++;
		mTaskType=taskType;
		mBaseTime=baseTime;
		mProcessTimeElapsed=0;
		mRealTimeElapsed=0;
		mCoresPerInstance=coresPerinstance;
		mRamPerInstance=ramPerInstance;
		mInstances=new ArrayList<>();

		for(int i=0;i<instanceNumbers;++i){
			mInstances.add(new TaskInstance(this,mCoresPerInstance, mRamPerInstance));
		}
		mAge=0;
	}
	
	public int getTaskId(){
		return mTaskID;
	}
	
	public TaskType getmTaskType() {
		return mTaskType;
	}

	public double getmBaseTime() {
		return mBaseTime;
	}
	
	public int getmRealTimeElapsed(){
		return mRealTimeElapsed;
	}
	
	public int getmAge(){
		return mAge;
	}
	
	public void incrementAge(){
		++mAge;
	}
	
	public void addProcessTime(double affinity){
		if(!isFinished()){
			mProcessTimeElapsed+=affinity;
			++mRealTimeElapsed;
		}
	}
	
	public boolean isFinished(){
		return mProcessTimeElapsed>=mBaseTime;
	}
	
	public int getTotalCoreFootprint(){
		return mInstances.size()*mCoresPerInstance;
	}
	
	public int getTotalRamFootprint(){
		return mInstances.size()*mCoresPerInstance;
	}
	
	public List<TaskInstance> getmInstances(){
		return mInstances;
	}

}
