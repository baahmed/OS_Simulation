package com.operatingsystem.java;

import com.process.java.Process;
import com.process.java.ProcessState;
import com.scheduler.java.Scheduler;
import com.semaphore.java.BinarySemaphore;
import com.semaphore.java.Type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class OperatingSystem {

    public static ArrayList<Thread> ProcessTable;

    private static volatile Queue<Process> readyQueue = new ConcurrentLinkedQueue<>();
    private static volatile Queue<Process> blockedReadQueue = new ConcurrentLinkedQueue<>();
    private static volatile Queue<Process> blockedWriteQueue = new ConcurrentLinkedQueue<>();
    private static volatile Queue<Process> blockedPrintQueue = new ConcurrentLinkedQueue<>();
    private static volatile Queue<Process> blockedInputQueue = new ConcurrentLinkedQueue<>();
    private static volatile Queue<Process> terminatedQueue = new ConcurrentLinkedQueue<>();

    public static volatile BinarySemaphore read = new BinarySemaphore(Type.READ);
    public static volatile BinarySemaphore write = new BinarySemaphore(Type.WRITE);
    public static volatile BinarySemaphore print = new BinarySemaphore(Type.PRINT);
    public static volatile BinarySemaphore input = new BinarySemaphore(Type.INPUT);

    public static Queue<Process> getBlockedReadQueue() {
        return blockedReadQueue;
    }

    public static Queue<Process> getBlockedWriteQueue() {
        return blockedWriteQueue;
    }

    public static Queue<Process> getBlockedPrintQueue() {
        return blockedPrintQueue;
    }

    public static Queue<Process> getBlockedInputQueue() {
        return blockedInputQueue;
    }

    public static Queue<Process> getReadyQueue() {
        return readyQueue;
    }

    public static Queue<Process> getTerminatedQueue() {
        return terminatedQueue;
    }

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


    //	public static int activeProcess= 0;
    //system calls:
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

    //3- print to console
    @SuppressWarnings("unused")
    public static void printText(String text) {

        System.out.println(text);

    }

    //4- take input

    @SuppressWarnings("unused")
    public static String TakeInput() {
        Scanner in = new Scanner(System.in);
        String data = in.nextLine();
        return data;

    }

    private static void createProcess(int processID) {
        Process process = new Process(processID);
        ProcessTable.add(process);
        process.setState(ProcessState.READY);
        Scheduler.scheduling(process);
    }

    public static void main(String[] args) {
        ProcessTable = new ArrayList<>();
        createProcess(1);
        createProcess(2);
        createProcess(3);
        createProcess(4);
        createProcess(5);
    }
}