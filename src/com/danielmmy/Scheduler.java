package com.danielmmy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Scheduler{
	protected static final int TEST_NUMBER_OF_PROCESS=1000;
	protected static final int TEST_NUMBER_OF_NODES=1000;
	protected static final int TEST_NUMBER_OF_CORES=12;
	protected static final int TEST_RAM_SIZE=36*1024;
	
	public enum AffinityType{
		LOW,NORMAL,HIGH
	}
	
	
	/*
	 * Affinity Arrays
	 */
	//Affinity array max of 2 tasks running concurrently on a specific node 
	//protected double[][] AFFINITY2=new double[Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER];
		
		//Affinity array max of 3 tasks running concurrently on a specific node 
	protected double[][][] AFFINITY3=new double[Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER];
	
	
	protected List<Task> mTasks;//Stores all Tasks 
	protected LinkedList<Task> mWaitingQueue;//Stores tasks to execute
	protected List<Task> mRunningQueue;//Stores running tasks
	private List<Task> mFinishedTasks;//Stores fished tasks
	private List<Integer> mArrivalOrder;//list of integers containing the max index of task available for processing i.e.(3,7,16,...,1000) for a 1000 task run
	protected List<Node> mInfrastructure;//list of nodes available
	protected int cycles;//Cycles passed since processing started
	protected String mOutputFileName;
	protected AffinityType mAffinityType;
	
	public Scheduler(List<Task> tasks, List<Integer> arrivalOrder, List<Node> infrastructure, String outputFileName, double[][][] AFFINITY3, AffinityType affinityType ){
		mTasks=tasks;
		mWaitingQueue=new LinkedList<>();
		mRunningQueue=new ArrayList<>();
		mFinishedTasks=new ArrayList<>();
		mArrivalOrder=arrivalOrder;
		mInfrastructure=infrastructure;
		cycles=0;
		mOutputFileName=outputFileName;
		this.AFFINITY3=AFFINITY3;
		mAffinityType=affinityType;
	}
	
	
	public final void runScheduler(){
		while(!isFinished()){
			updateAvailableTasks();
			removeFinishedTasks();
			allocateTask();
			processTasks();
			++cycles;
			//System.out.println("Tasks:"+mTasks.size()+", Wainting:"+mWaitingQueue.size()+", Running:"+mRunningQueue.size()+", Finished:"+mFinishedTasks.size());
		}
		saveResults();
	}
	
	/*
	 * Check if processing has finished
	 */
	private boolean isFinished(){
		return mTasks.size()==mFinishedTasks.size();
	}
	
	/*
	 * insert task into available to be run tasks
	 */
	private void updateAvailableTasks(){
		if(cycles<mArrivalOrder.size()){
			int maxIndex=mArrivalOrder.get(cycles);
			for(int i=(cycles>0)?mArrivalOrder.get(cycles-1):0;i<maxIndex;++i){
				mWaitingQueue.add(mTasks.get(i));
			}
		}
	}
	
	/*
	 * Remove finished task from running queue
	 */
	private void removeFinishedTasks(){
		Iterator<Task> iterator=mRunningQueue.iterator();
		Task t;
		while(iterator.hasNext()){
			t=iterator.next();
			if(t.isFinished()){
				mFinishedTasks.add(t);
				iterator.remove();
				freeNodes(t);
			}
		}
	}
	
	/*
	 * Allocates Tasks instances to be executed. Must be overridden by appropriate scheduling algorithm 
	 */
	protected abstract void allocateTask();
	
	/*
	 * Process running tasks
	 */
	private void processTasks(){
		for(Node n: mInfrastructure)
			n.processTasks(AFFINITY3);
	}
	
	
	
	/*
	 * Helper method to removeFinishedTasks
	 */
	private void freeNodes(Task task){
		for(Node n: mInfrastructure){	
			n.removeTask(task);
		}
	}
	
	

	
	/*
	 * save results to file
	 */
	protected void saveResults(){
		DateFormat dateFormat = new SimpleDateFormat("_yyyy_MM_dd_HH:mm:ss");
		Date date = new Date();
		try(BufferedWriter bw=new BufferedWriter(new FileWriter(mOutputFileName+dateFormat.format(date)))){
			bw.write("Total processing time: "+cycles+" TU");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Check for minimum integer in an array of at least two integers
	 */
	protected int min(int...values) throws IndexOutOfBoundsException{
		if(values.length<2)
			throw new IndexOutOfBoundsException();
		int min=values[0];
		for(int i=1;i<values.length;++i){
			if (min>values[i])
				min=values[i];
		}
		return min;
	}
}
