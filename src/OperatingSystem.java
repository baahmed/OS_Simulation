import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class OperatingSystem {
	
	 public static ArrayList<Thread> ProcessTable = new ArrayList<Thread>();

	    //queues used for the scheduling algorithm
	    /*
	     * pro tip :P
	     * LinkedList.removeFirst(); - removes the first item that was added to the queue
	     *
	     * LinkedList.addLast - adds to the end of the linked list, check out its use:
	     * https://www.geeksforgeeks.org/linkedlist-addlast-method-in-java/
	     *
	     * TODO: dont forget to initialize stuff and dont forget to handle the empty list case
	     *
	     */

	    public static LinkedList<Process> readyQueue = new LinkedList<>();
	    public static LinkedList<Process> blockedReadQueue = new LinkedList<>();
	    public static LinkedList<Process> blockedWriteQueue = new LinkedList<>();
	    public static LinkedList<Process> blockedPrintQueue = new LinkedList<>();
	    public static LinkedList<Process> blockedInputQueue = new LinkedList<>();

	    /*used semaphores*/

	    public static BinarySemaphore read = new BinarySemaphore();
	    public static BinarySemaphore write = new BinarySemaphore();
	    public static BinarySemaphore print = new BinarySemaphore();
	    public static BinarySemaphore input = new BinarySemaphore();

	    //getters for each queue reference
	    //semaphores are only responsible for adding stuff in the blocked queue and once unblocked, to the ready queue

	    public static LinkedList<Process> getBlockedReadQueue() {
	        return blockedReadQueue;
	    }


	    public static LinkedList<Process> getBlockedWriteQueue() {
	        return blockedWriteQueue;
	    }

	    public static LinkedList<Process> getBlockedPrintQueue() {
	        return blockedPrintQueue;
	    }

	    public static LinkedList<Process> getBlockedInputQueue() {
	        return blockedInputQueue;
	    }

	    public static LinkedList<Process> getReadyQueue() {
	        return readyQueue;
	    }

	    //getters for each semaphore
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


	    //basant didnt write the rest of the comments
//		public static int activeProcess= 0;
		//system calls:
		// 1- Read from File
		@SuppressWarnings("unused")
		public static String readFile(String name) {
			String Data="";
			File file = new File(name);
		 try {
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine())
			{
				Data+= scan.nextLine()+"\n";
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
			try
			{
				BufferedWriter BW = new BufferedWriter(new FileWriter(name));
				BW.write(data);
				BW.close();
			} 
			catch (IOException e) 
			{
				System.out.println(e.getMessage());
			}

		}
		//3- print to console
		@SuppressWarnings("unused")
		public static void printText(String text) {

			System.out.println(text);
			
		}
		
		//4- take input
		
		@SuppressWarnings("unused")
		public static String TakeInput() {
			Scanner in= new Scanner(System.in);
			String data = in.nextLine();
			return data;
			
		}
		
		private static void createProcess(int processID){
			Process p = new Process(processID);
			ProcessTable.add(p);
			Process.setProcessState(p,ProcessState.Ready);
			p.start();
			
		}
		
		public static void main(String[] args) {
//	   		ProcessTable = new ArrayList<Thread>();
//
//			createProcess(1);
//			createProcess(2);
//			createProcess(3);
//			createProcess(4);
//			createProcess(5);
			
			Process p1 = new Process(1);
			Process p2 = new Process(2);
			Process p3 = new Process(1);
			
			p2.start();
			p1.start();
			p3.start();

		}
	 
}
