import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OperatingSystem {
	
	//process currently executed by processor
	private static Process execProc;

	public static ArrayList<Thread> ProcessTable = new ArrayList<Thread>();

	/*
	 * queues used by the OS. We have the ready queue, which contains all the
	 * processes that are ready to be selected to execute. We have four blocked
	 * queues for each of the four critical resources available to us. 
	 * 
	 * I used ConcurrentLinkedQueue because it is a thread safe data structure.
	 */

	public static ConcurrentLinkedQueue<Process> readyQueue = new ConcurrentLinkedQueue<>();
	public static ConcurrentLinkedQueue<Process> blockedReadQueue = new ConcurrentLinkedQueue<>();
	public static ConcurrentLinkedQueue<Process> blockedWriteQueue = new ConcurrentLinkedQueue<>();
	public static ConcurrentLinkedQueue<Process> blockedPrintQueue = new ConcurrentLinkedQueue<>();
	public static ConcurrentLinkedQueue<Process> blockedInputQueue = new ConcurrentLinkedQueue<>();

	
	
	
	/* 
	 * four semaphores to govern the use of the four resources. Each semaphore 
	 * has its own value (0 taken or 1 available) as well as a pointer to the 
	 * next process to claim the resource (if any)
	 * 
	 *semaphores are only responsible for adding stuff in the blocked queue and
	 *once unblocked, to the ready queue. The ready queue is the responsibility
	 *of the scheduler itself.
	 */

	public static BinarySemaphore read = new BinarySemaphore();
	public static BinarySemaphore write = new BinarySemaphore();
	public static BinarySemaphore print = new BinarySemaphore();
	public static BinarySemaphore input = new BinarySemaphore();

	
	
	
	/*getters for each queue reference*/
	public static ConcurrentLinkedQueue<Process> getBlockedReadQueue() {
		return blockedReadQueue;
	}

	public static ConcurrentLinkedQueue<Process> getBlockedWriteQueue() {
		return blockedWriteQueue;
	}

	public static ConcurrentLinkedQueue<Process> getBlockedPrintQueue() {
		return blockedPrintQueue;
	}

	public static ConcurrentLinkedQueue<Process> getBlockedInputQueue() {
		return blockedInputQueue;
	}

	public static ConcurrentLinkedQueue<Process> getReadyQueue() {
		return readyQueue;
	}

	
	
	
	/*getters for each semaphore.*/
	public static BinarySemaphore getReadSemaphore() {
		return read;
	}

	public static BinarySemaphore getWriteSemaphore() {
		return write;
	}

	public static BinarySemaphore getPrintSemaphore() {
		return print;
	}

	public static BinarySemaphore getInputSemaphore() {
		return input;
	}


//		public static int activeProcess= 0;
	// system calls:
	// 1- Read from File
	@SuppressWarnings("unused")
	public static String readFile(String name) {
		String Data = "";
		File file = new File(name);
		try {
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine()) {
				Data += scan.nextLine() + "\n";
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return Data;
	}

	// 2- Write into file
	@SuppressWarnings("unused")
	public static void writefile(String name, String data) {
		try {
			BufferedWriter BW = new BufferedWriter(new FileWriter(name));
			BW.write(data);
			BW.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	// 3- print to console
	@SuppressWarnings("unused")
	public static void printText(String text) {

		System.out.println(text);

	}

	// 4- take input

	@SuppressWarnings("unused")
	public static String TakeInput() {
		Scanner in = new Scanner(System.in);
		String data = in.nextLine();
		return data;

	}

	private static void createProcess(int processID) {
		
//		System.out.println("creating process " + processID);
		
		Process p = new Process(processID);
		ProcessTable.add(p);
		Process.setProcessState(p, ProcessState.Ready);
		readyQueue.add(p);
		
		//TODO: uncomment this when testing semaphores only
		//start should be inside scheduler
		p.start();
		
		//TODO: comment this when testing semaphores only
		//schedule();

	}

	
//-------------------------------------------SCHEDULER---------------------------------------------------------------------
		
	//assigns a process from the readyQueue to execute
	//using the FCFS algorithm implemented by using a queue
	
	//this method is called when:
	//creating a process
	//posting a semaphore
	//once a process is terminated
	
	
	public static void schedule() 
	{
//		System.out.println("entering scheduler");
//		System.out.println("ready queue size: " + readyQueue.size());
		
		// if there is currently a process being executed
		// check if it was terminated or blocked
		// and "free" the processor
		if (execProc != null) {
			
//			System.out.println("current process state: " + Process.getProcessState(execProc));
			
			if (Process.getProcessState(execProc) == ProcessState.Terminated
					|| Process.getProcessState(execProc) == ProcessState.Waiting)
				execProc = null;

		}


		// if there is currently a process in execution
		// or the ready queue is empty
		// then the scheduler cannot schedule any processor currently
		if (execProc != null || readyQueue.isEmpty())
			return;

		// remove the process that was added first
		execProc = readyQueue.poll();

		// set the process state to running
		Process.setProcessState(execProc, ProcessState.Running);

		// if process was suspended due to block, resume
		// else start running the process
		if (execProc.isAlive())
			execProc.resume();
		else
			execProc.start();
		
//		System.out.println("executing a process");

	}

	public static void main(String[] args) {
		
		/* To test Sempahores only
		 * 
		 * In the OperatingSystem class:
		 * * in createProcess method:
		 * * * uncomment p.start()
		 * * * comment out schedule()
		 * * comment out the schedule() method
		 * 
		 * In the Process class:
		 * * in the setProcessState method:
		 * * * comment out OperatingSystem.schedule()
		 * 
		 * In the BinarySemaphore class:
		 * * in the semWait method 
		 * * * comment out OperatingSystem.schedule()
		 * * * comment out p.suspend()
		 */
		
		ProcessTable = new ArrayList<Thread>();
//
//			createProcess(1);
//			createProcess(2);
//			createProcess(3);
//			createProcess(4);
//			createProcess(5);

		// semaphore test cases
		// please make sure to uncomment termination when testing :-))
//			Process p1 = new Process(1);////
//			Process p2 = new Process(2);////
//			Process p3 = new Process(1);////
//			Process p4 = new Process(3);////
//			Process p5 = new Process(4);////
//			Process p6 = new Process(5);////
//			Process p7 = new Process(4);////
//			Process p8 = new Process(1);////
//			Process p9 = new Process(2);////
//			Process p10 = new Process(1);////
//			
//			
//			p9.start();
//			p1.start();
//			p3.start();
//			p10.start();
//			p4.start();
//			p5.start();
//			p6.start();
//			p7.start();
//			p2.start();
//			p8.start();

		
		//testing scheduler
		//should print the content of a file 2 times
		//followed by entering data to a file once
		//followed by printing numbers 0 to 300 twice
		//followed by printing the content of a file 3 times
		//followed by printing 500 to 1000 twice
		//followed by asking the lower bound and upper bound twice
//		createProcess(1);
//		createProcess(1);
//		createProcess(2);
//		createProcess(3);
//		createProcess(3);
//		createProcess(1);
//		createProcess(1);
//		createProcess(1);
//		createProcess(4);
//		createProcess(4);
//		createProcess(5);
//		createProcess(5);
		
		
//		createProcess(1);
//		createProcess(1);
//		createProcess(2);
//		createProcess(3);
//		createProcess(5);
//		createProcess(4);
		
		
		///////////REPORT TEST CASES
		
		
		//TEST CASE 2
		//createProcess(1);
		//createProcess(3);
		
		//TEST CASE 3
		createProcess(3); //print 0 to 300
		createProcess(4); //print 500 to 1000
		createProcess(5);


		
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}

}
