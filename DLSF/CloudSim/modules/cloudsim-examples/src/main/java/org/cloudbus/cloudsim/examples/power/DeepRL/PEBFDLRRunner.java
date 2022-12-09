package org.cloudbus.cloudsim.examples.power.DeepRL;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.power.Constants;
import org.cloudbus.cloudsim.examples.power.Helper;
import org.cloudbus.cloudsim.examples.power.RunnerAbstract;
import org.cloudbus.cloudsim.power.DRLDatacenter;
import org.cloudbus.cloudsim.power.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import static org.cloudbus.cloudsim.examples.power.DeepRL.DeepRLHelper.rnd;

/**
 * @author Swarnavalli Srinivasan
 * @since Nov 26, 2022
 */

public class PEBFDLRRunner extends RunnerAbstract {

    /** The broker. */
    protected static DRLDatacenterBroker broker;

    /** The cloudlet list. */
    protected static List<DRLCloudlet> cloudletList;

    public static boolean dynamic = true;

    public static String inputFolder = "";

    /**
     * @param enableOutput enable output or not
     * @param outputToFile output to file or not
     * @param inputFolder path of input folder
     * @param outputFolder path of output folder
     * @param workload workload name
     * @param containerAllocationPolicy allocation policy name
     * @param containerSelectionPolicy selection policy name
     * @param parameter parameter for running tests
     */
    public PEBFDLRRunner(
            boolean enableOutput,

            boolean outputToFile,
            String inputFolder,
            String outputFolder,
            String workload,
            String containerAllocationPolicy,
            String containerSelectionPolicy,
            String parameter) {
        super(
                enableOutput,
                outputToFile,
                inputFolder,
                outputFolder,
                workload,
                containerAllocationPolicy,
                containerSelectionPolicy,
                parameter);
    }

    @Override
    protected void init(String inputFolder) {
        try {
            CloudSim.init(1, Calendar.getInstance(), false);

            broker = createBroker("Broker");
            int brokerId = broker.getId();

            // Data center creation at RunnerAbstract.start()
            cloudletList = dynamic ? DeepRLHelper.createCloudletListBitBrainDynamic(brokerId, inputFolder, 0)
            : DeepRLHelper.createCloudletListBitBrain(brokerId, inputFolder);
            vmList = DeepRLHelper.createVmList(brokerId, cloudletList.size(), 0);
            hostList = DeepRLHelper.createHostList(DeepRLConstants.NUMBER_OF_HOSTS);
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
            System.exit(0);
        }
    }

    /**
     * Starts the simulation.
     *
     * @param experimentName the experiment name
     * @param outputFolder the output folder
     * @param containerAllocationPolicy the vm allocation policy
     */
    @Override
    protected void start(String experimentName, String outputFolder,VmAllocationPolicy containerAllocationPolicy) {
        if(dynamic)
            startDynamic(experimentName, outputFolder, containerAllocationPolicy);
        else
            startStatic(experimentName, outputFolder, containerAllocationPolicy);
    }

    /**
     * Starts a static simulation.
     *
     * @param experimentName the experiment name
     * @param outputFolder the output folder
     * @param containerAllocationPolicy the container allocation policy
     */
    protected void startStatic(String experimentName, String outputFolder, VmAllocationPolicy containerAllocationPolicy) {
        System.out.println("Starting " + experimentName);

        try {
            DRLDatacenter datacenter = (DRLDatacenter) DeepRLHelper.createDatacenter(
                    "Datacenter",
                    DRLDatacenter.class,
                    hostList,
                    containerAllocationPolicy,
                    broker);

            datacenter.setDisableMigrations(false);

            broker.submitVmList(vmList);
            broker.submitCloudletList(cloudletList);

            CloudSim.terminateSimulation(DeepRLConstants.SIMULATION_LIMIT);
            double lastClock = CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();
            Log.printLine("Received " + newList.size() + " cloudlets");

            CloudSim.stopSimulation();

            Helper.printResults(
                    datacenter,
                    vmList,
                    lastClock,
                    experimentName,
                    Constants.OUTPUT_CSV,
                    outputFolder);

            Log.printLine("Number of Containers left (datacenter) : " + datacenter.getVmList().size());
            Log.printLine("Number of Containers left (broker) : " + broker.getVmList().size());

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
            System.exit(0);
        }

        Log.printLine("Finished " + experimentName);
    }

    /**
     * Starts a dynamic simulation.
     *
     * @param experimentName the experiment name
     * @param outputFolder the output folder
     * @param containerAllocationPolicy the container allocation policy
     */
    protected void startDynamic(String experimentName, String outputFolder, VmAllocationPolicy containerAllocationPolicy) {
        System.out.println("Starting " + experimentName);

        try {
            DRLDatacenter datacenter = (DRLDatacenter) DeepRLHelper.createDatacenter(
                    "Datacenter",
                    DRLDatacenter.class,
                    hostList,
                    containerAllocationPolicy,
                    broker);

            datacenter.setDisableMigrations(false);

            broker.submitVmList(vmList);
            broker.submitCloudletList(cloudletList);

            System.out.println("Creating Containers...");
            DecimalFormat decimalFormat = new DecimalFormat("###");

            for(int i = 300; i < DeepRLConstants.SIMULATION_LIMIT; i+=300) {
                int brokerId = broker.getId();

                List<DRLCloudlet> cloudletListDynamic = DeepRLHelper.createCloudletListBitBrainDynamic(brokerId, PEBFDLRRunner.inputFolder, 0);
                if(cloudletListDynamic.size() == 0){
                    continue;
                }
                List<Vm> vmListDynamic = DeepRLHelper.createVmList(brokerId, cloudletListDynamic.size(), 0);
//                cloudletList.addAll(cloudletListDynamic);
//                vmList.addAll(vmListDynamic);
                broker.createVmsAfter(vmListDynamic, i);
                broker.destroyVMsAfter(vmListDynamic, i+Math.max(300,(int) (rnd.nextGaussian() * DeepRLConstants.vmTimestdGaussian + DeepRLConstants.vmTimemeanGaussian)));
                broker.submitCloudletList(cloudletListDynamic, i);
                System.out.print('\r' + decimalFormat.format((int)(100*i/DeepRLConstants.SIMULATION_LIMIT)) + "%");
            }

            System.out.println();


            CloudSim.terminateSimulation(DeepRLConstants.SIMULATION_LIMIT);
            double lastClock = CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();
            Log.printLine("Received " + newList.size() + " cloudlets at start");

            CloudSim.stopSimulation();

            Helper.printResults(
                    datacenter,
                    vmList,
                    lastClock,
                    experimentName,
                    Constants.OUTPUT_CSV,
                    outputFolder);

            Log.printLine("Number of Containers left (datacenter) : " + datacenter.getVmList().size());
            Log.printLine("Number of Containers left (broker) : " + broker.getVmList().size());

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
            System.exit(0);
        }

        Log.printLine("Finished " + experimentName);
    }

    private static DRLDatacenterBroker createBroker(String name){

        DRLDatacenterBroker broker = null;
        try {
            broker = new DRLDatacenterBroker(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    /**
     * Gets the container allocation policy.
     *
     * @param containerAllocationPolicyName the container allocation policy name
     * @param containerSelectionPolicyName the container selection policy name
     * @param parameterName the parameter name
     * @return the container allocation policy
     */
    @Override
    protected VmAllocationPolicy getVmAllocationPolicy(
            String containerAllocationPolicyName,
            String containerSelectionPolicyName,
            String parameterName) {
        VmAllocationPolicy containerAllocationPolicy = null;
        PowerVmSelectionPolicy containerSelectionPolicy = null;
        if (!containerSelectionPolicyName.isEmpty()) {
            containerSelectionPolicy = getVmSelectionPolicy(containerSelectionPolicyName);
        }
        double parameter = 0;
        if (!parameterName.isEmpty()) {
            parameter = Double.valueOf(parameterName);
        }
        if (containerAllocationPolicyName.equals("iqr")) {
            PowerVmAllocationPolicyMigrationAbstract fallbackContainerSelectionPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
                    hostList,
                    containerSelectionPolicy,
                    0.7);
            containerAllocationPolicy = new PowerVmAllocationPolicyMigrationInterQuartileRange(
                    hostList,
                    containerSelectionPolicy,
                    parameter,
                    fallbackContainerSelectionPolicy);
        } else if (containerAllocationPolicyName.equals("mad")) {
            PowerVmAllocationPolicyMigrationAbstract fallbackContainerSelectionPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
                    hostList,
                    containerSelectionPolicy,
                    0.7);
            containerAllocationPolicy = new PowerVmAllocationPolicyMigrationMedianAbsoluteDeviation(
                    hostList,
                    containerSelectionPolicy,
                    parameter,
                    fallbackContainerSelectionPolicy);
        } else if (containerAllocationPolicyName.equals("lr")) {
            PowerVmAllocationPolicyMigrationAbstract fallbackContainerSelectionPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
                    hostList,
                    containerSelectionPolicy,
                    0.7);
            containerAllocationPolicy = new PowerVmAllocationPolicyMigrationLocalRegression(
                    hostList,
                    containerSelectionPolicy,
                    parameter,
                    Constants.SCHEDULING_INTERVAL,
                    fallbackContainerSelectionPolicy);
        } else if (containerAllocationPolicyName.equals("lrr")) {
            PowerVmAllocationPolicyMigrationAbstract fallbackContainerSelectionPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
                    hostList,
                    containerSelectionPolicy,
                    0.7);
            containerAllocationPolicy = new PowerVmAllocationPolicyMigrationLocalRegressionRobust(
                    hostList,
                    containerSelectionPolicy,
                    parameter,
                    Constants.SCHEDULING_INTERVAL,
                    fallbackContainerSelectionPolicy);
        } else if (containerAllocationPolicyName.equals("thr")) {
            containerAllocationPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
                    hostList,
                    containerSelectionPolicy,
                    parameter);
        } else if (containerAllocationPolicyName.equals("dvfs")) {
            containerAllocationPolicy = new PowerVmAllocationPolicySimple(hostList);
        } else if (containerAllocationPolicyName.equals("PEBFDlr")) {
            PowerVmAllocationPolicyPEBFDMigrationAbstract fallbackContainerSelectionPolicy = new PowerVmAllocationPolicyMigrationPEBFDStaticThreshold(
                    hostList,
                    containerSelectionPolicy,
                    0.7);
            containerAllocationPolicy = new PowerContainerAllocationPolicyMigrationPEBFDLocalRegression(
                    hostList,
                    containerSelectionPolicy,
                    parameter,
                    Constants.SCHEDULING_INTERVAL,
                    fallbackContainerSelectionPolicy);
        }

        else if(containerAllocationPolicyName.equals("deepRL-alloc")){
            PowerVmAllocationPolicyMigrationAbstract fallbackContainerSelectionPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
                    hostList,
                    containerSelectionPolicy,
                    0.7);
            containerAllocationPolicy = new DRLVmAllocationPolicy(hostList, (DRLVmSelectionPolicy)containerSelectionPolicy, fallbackContainerSelectionPolicy);
        } else {
            System.out.println("Unknown Container allocation policy: " + containerAllocationPolicyName);
            System.exit(0);
        }
        return containerAllocationPolicy;
    }

    /**
     * Gets the container selection policy.
     *
     * @param containerSelectionPolicyName the container selection policy name
     * @return container  selection policy
     */
    @Override
    protected PowerVmSelectionPolicy getVmSelectionPolicy(String containerSelectionPolicyName) {
        PowerVmSelectionPolicy containerSelectionPolicy = null;
        if (containerSelectionPolicyName.equals("mc")) {
            containerSelectionPolicy = new PowerVmSelectionPolicyMaximumCorrelation(
                    new PowerVmSelectionPolicyMinimumMigrationTime());
        } else if (containerSelectionPolicyName.equals("mmt")) {
            containerSelectionPolicy = new PowerVmSelectionPolicyMinimumMigrationTime();
        } else if (containerSelectionPolicyName.equals("mu")) {
            containerSelectionPolicy = new PowerVmSelectionPolicyMinimumUtilization();
        } else if (containerSelectionPolicyName.equals("rs")) {
            containerSelectionPolicy = new PowerVmSelectionPolicyRandomSelection();
        }
        else if(containerSelectionPolicyName.equals("deepRL-sel")){
            containerSelectionPolicy = new DRLVmSelectionPolicy();
        } else {
            System.out.println("Unknown Conatiner selection policy: " + containerSelectionPolicyName);
            System.exit(0);
        }
        return containerSelectionPolicy;
    }

    public static void main(String[] args) throws IOException {
        boolean enableOutput = true;
        boolean outputToFile = false;
        String inputFolder = "modules\\cloudsim-examples\\src\\main\\java\\workload\\bitbrain";//PEBFDLRRunner.class.getClassLoader().getResource("workload/bitbrain").getPath();
        String outputFolder = "output";
        String workload = "fastStorage\\2013-8"; // Random workload
        String containerAllocationPolicy =  "PEBFDlr"; // Local Regression (LR) VM allocation policy
        String containerSelectionPolicy = "mmt"; // Minimum Migration Time (MMT) VM selection policy
        String parameter = "200"; // the safety parameter of the LR policy
        dynamic = true; // Dynamic or static simulation (Change the cloudlet lengths accordingly)

        PEBFDLRRunner.inputFolder = inputFolder + "/" + workload;

        Log.setDisabled(false);
        OutputStream os = new FileOutputStream("output.txt");
        OutputStream os2 = new FileOutputStream("DL.txt");
        Log.setOutput(os);
        Log.setOutput2(os2);

        new PEBFDLRRunner(
                enableOutput,
                outputToFile,
                inputFolder,
                outputFolder,
                workload,
                containerAllocationPolicy,
                containerSelectionPolicy,
                parameter);
    }

}
