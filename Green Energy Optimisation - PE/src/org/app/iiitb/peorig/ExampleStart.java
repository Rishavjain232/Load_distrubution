package org.app.iiitb.peorig;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;


public class ExampleStart {

	static final String COMMENT = ";"; 
    private static int JOB_NUM = 1 - 1; 
    private static int SUBMIT_TIME = 2 - 1; 
    private static final int RUN_TIME = 4 - 1;
//    private static final int NUM_PROC = 5 - 1; 
//    private static int REQ_NUM_PROC = 8 - 1; 
//    private static int REQ_RUN_TIME = 9 - 1; 
//    private static final int USER_ID = 12 - 1;
//    private static final int GROUP_ID = 13 - 1; 
    private static int MAX_FIELD = 18; 
	
	public static void main(String[] args) throws IOException {
		XMLReaderDom xrd=new XMLReaderDom();
		List<DataCenterDetails> dsd=xrd.getDataCenterDetailsXML("datacenters.xml");

		System.out.println(Calendar.SECOND);

		int num_user = 3;
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;
        CloudSim.init(num_user, calendar, trace_flag);
  
        //Creating 3 Datacenters - each one has 1 host with 5 PEs
        Datacenter dc[]=new Datacenter[dsd.size()];
        DatacenterBroker broker[]=new DatacenterBroker[dsd.size()];
        int ite=0;
        TreeMap<DataCenterDetails, DatacenterBroker> dsdBrokerList=
        		new TreeMap<DataCenterDetails,DatacenterBroker>(new Comparator<DataCenterDetails>() {

			@Override
			public int compare(DataCenterDetails o1, DataCenterDetails o2) {
				return (int)(o2.getGreenEnergy()-o1.getGreenEnergy());
			}
		});
        for(DataCenterDetails d:dsd) {
        	dc[ite] = createDatacenter(d.getId(), 1);
        	System.out.println(dc[ite].getId()+" "+dc[ite].getName());
        	broker[ite] = createBroker(d.getBroker());
        	dsdBrokerList.put(d, broker[ite]);
        	broker[ite].submitVmList(createVM(broker[ite].getId(), 5, 1));
        	ite++;
        }
        
        for(DataCenterDetails d:dsdBrokerList.keySet()) {
        	System.out.println(dsdBrokerList.get(d));
//        	System.out.println(d)
        	System.out.println(d);
        }
        List<Cloudlet> ls=createCloudletWithoutBroker(10, 21);
        ls.addAll(createCloudletWithoutBroker(10, 201));
        ls.addAll(createCloudletWithoutBroker(10, 2001));
        
        scheduleCloudlets(ls,dsdBrokerList);
        
        
        //Starting simulation here
        CloudSim.startSimulation();
//        System.out.println(CloudSim.getSimulationCalendar().getTime());
        List<Cloudlet> newList = new LinkedList<Cloudlet>();
        for(DatacenterBroker dcb:broker) {
        	newList.addAll(dcb.getCloudletReceivedList());
        }
        
        CloudSim.stopSimulation();
        
        printCloudletList(newList);
        
	}
	
	public static void scheduleCloudlets(List<Cloudlet> cloudletList, 
								TreeMap<DataCenterDetails, DatacenterBroker> dsdBrokerList) {
		Calendar now=Calendar.getInstance();
		double threshold=0.50;
		HashMap<DataCenterDetails, List<Cloudlet>> clmap = new HashMap<DataCenterDetails,List<Cloudlet>>();
		
		for(DataCenterDetails x:dsdBrokerList.keySet()) {
			clmap.put(x, new LinkedList<Cloudlet>());
		}

    	for(Cloudlet cloudlet : cloudletList) {
    		for(DataCenterDetails d:dsdBrokerList.keySet()) {
    			System.out.println(CloudSim.getSimulationCalendar().getTime()+" | "+now.get(Calendar.SECOND)+" | "+d.getCapacity()[now.get(Calendar.SECOND)-1]+" | "+threshold);
				
    			if(d.getCapacity()[now.get(Calendar.SECOND)-1] < threshold) {
    				cloudlet.setUserId(dsdBrokerList.get(d).getId());
    				clmap.get(d).add(cloudlet);
    				break;
    			}
    		}
//    		Entry<DataCenterDetails,Integer> ent = dsdBrokerList.firstEntry();
    	}
    	for(DataCenterDetails x:dsdBrokerList.keySet()) {
    		if(clmap.get(x).isEmpty())
    			continue;
    		dsdBrokerList.get(x).submitCloudletList(clmap.get(x));
		}
//    	brk2.submitCloudletList(createCloudlet(brk2.getId(), 5, 2001));    	
    }

    private static List<Vm> createVM(int userId, int vms, int idShift) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<Vm> list = new LinkedList<Vm>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[vms];
        
        for (int i = 0; i < vms; i++) {
            vm[i] = new Vm(idShift + i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }
        
        return list;
    }
    
    private static List<Cloudlet> createCloudletWithoutBroker(int cloudlets, int idShift) throws IOException {
    	LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();
    	BufferedReader reader = new BufferedReader(new InputStreamReader(
    									new FileInputStream("/home/prince/Downloads/HPC2N-2002-2.2-cln_v2.swf")));
    	//SDSC-BLUE-2000-4.2-cln.swf"
		//HPC2N-2002-2.1-cln2.swf"
        
        int lineNum = 1;
        String line = null;
        Cloudlet cloudlet;
        while (reader.ready() && (line = reader.readLine()) != null) {
            cloudlet=parseValue(line, lineNum);
            if(cloudlet!=null) {
            	list.add(cloudlet);
//            	System.out.println("----------------------------"+cloudlet);
            }
            lineNum++;
        }

        reader.close();
        return list;
//        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();
//
//        //cloudlet parameters
//        long length = 100000;
//        long fileSize = 0;
//        long outputSize = 0;
//        int pesNumber = 1;
//        UtilizationModel utilizationModel = new UtilizationModelFull();
//        
//        Cloudlet[] cloudlet = new Cloudlet[cloudlets];
//        
//        for (int i = 0; i < cloudlets; i++) {
//            cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
//            // setting the owner of these Cloudlets
////            cloudlet[i].setUserId(userId);
//            list.add(cloudlet[i]);
//        }
//        
//        return list;
    }
    
    static Cloudlet parseValue(final String line, final int lineNum) {
    	String[] fieldArray = new String[MAX_FIELD];
        // skip a comment line
        if (line.startsWith(COMMENT)) {
                return null;
        }

        final String[] sp = line.split("\\s+"); // split the fields based on a
        // space
        int len = 0; // length of a string
        int index = 0; // the index of an array

        // check for each field in the array
        for (final String elem : sp) {
                len = elem.length(); // get the length of a string

                // if it is empty then ignore
                if (len == 0) {
                        continue;
                }
                fieldArray[index] = elem;
                index++;
        }

        if (index == MAX_FIELD) {
                return extractField(fieldArray, lineNum);
        }
        return null;
}
    
    private static Cloudlet extractField(final String[] array, final int line) {
    	int pesNumber = 1;
        long fileSize = 0;
        long outputSize = 0;
    	
    	int id=new Integer(array[JOB_NUM].trim());
    	long submitTime = new Long(array[SUBMIT_TIME].trim());
//    	int reqRunTime=new Integer(array[REQ_RUN_TIME].trim());
    	int length=new Integer(array[RUN_TIME].trim());
//    	int userID = new Integer(array[USER_ID].trim()).intValue();
    	
//    	System.out.println(id+" "+submitTime+" "+length);
    	UtilizationModel utilizationModel = new UtilizationModelFull();
    	
    	Cloudlet cloudlet = new Cloudlet(id, length*1000, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
    	return cloudlet;
}
    
    private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift) throws FileNotFoundException {
        // Creates a container to store Cloudlets
//		List<Cloudlet> cloudletList;
//		//Read Cloudlets from workload file in the swf format
//		WorkloadFileReader workloadFileReader = new WorkloadFileReader("/home/prince/Downloads/HPC2N-2002-2.2-cln_v2.swf",1);
//				//SDSC-BLUE-2000-4.2-cln.swf", 1);
//				//HPC2N-2002-2.1-cln2.swf"
//		//generate cloudlets from workload file
//		cloudletList = workloadFileReader.generateWorkload();
//		for(Cloudlet cl:cloudletList) {
//			cl.setUserId(userId);
//			cl.setSubmissionTime(342072);
//			System.out.println(cl.getCloudletId()+" ------------ "+cl.getSubmissionTime());
//			
//			
//		}
//		return cloudletList;
    	
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

        //cloudlet parameters
        long length = 100000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        
        Cloudlet[] cloudlet = new Cloudlet[cloudlets];
        
        for (int i = 0; i < cloudlets; i++) {
            cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            System.out.println(cloudlet[i].getCloudletId()+" ------------ "+cloudlet[i].getSubmissionTime());
            list.add(cloudlet[i]);
        }
        
        return list;
    }
    
    private static Datacenter createDatacenter(String name, int hostNumber) {
        
        
        List<Host> hostList = new ArrayList<Host>();
        List<Pe> peList1 = new ArrayList<Pe>();
        
        int mips = 1000;
        int hostId = 0;
        int ram = 16384;
        long storage = 1000000;
        int bw = 10000;
        
        peList1.add(new Pe(0, new PeProvisionerSimple(mips)));
        peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
        peList1.add(new Pe(2, new PeProvisionerSimple(mips)));
        peList1.add(new Pe(3, new PeProvisionerSimple(mips)));
        peList1.add(new Pe(4, new PeProvisionerSimple(mips)));
        
        for (int i = 0; i < hostNumber; i++) {
            hostList.add(
                    new Host(
                    hostId,
                    new RamProvisionerSimple(ram),
                    new BwProvisionerSimple(bw),
                    storage,
                    peList1,
                    new VmSchedulerSpaceShared(peList1)));
            
            hostId++;
        }
        
        
        String arch = "x86";      // system architecture
        String os = "Linux";          // operating system
        String vmm = "Xen";
        double time_zone = 10.0;         // time zone this resource located
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.1;	// the cost of using storage in this resource
        double costPerBw = 0.1;			// the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
        
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return datacenter;
    }
    
    private static DatacenterBroker createBroker(String name) {
        
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    /**
     * Prints the Cloudlet objects
     *
     * @param list list of Cloudlets
     */
    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;
        
        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + indent + "Time" + indent + "Start Time" + indent + "Finish Time");
        
        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);
            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
//            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");
                
                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent + indent + dft.format(cloudlet.getActualCPUTime())
                        + indent + indent + dft.format(cloudlet.getExecStartTime()) + indent + indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }
    }

}

