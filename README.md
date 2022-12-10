#  Automated resource allocation for Serverless Containers in the Edge
Serverless Container which is also known as Function-as-a-Service (FaaS) gives the opportunity to write and update a piece of code on the fly and execute it in future as a response to an event like API trigger without having to worry about underlying architecture. Serverless applications run on Worker nodes which are temporary containers and it is up to platform providers to manage them. The issue here, however, is that there is a delay in the creation of these worker nodes as temporary containers ready for your initial invocation. Among the proposed efficient resource allocation algorithm for serverless container Dynamic Scheduling for Stochastic Edge-Cloud Computing Environments using A3C  learning and Residual Recurrent Neural Networks is one of  the  promising  approaches. Both the Residual Recurrent Neural Network (R2N2) and the Asynchronous- Advantage-Actor-Critical (A3C) learning proposed in  this  approach  are  known for updating model parameters quickly. In order to support decentralized learning across numerous agents in stochastic Edge-Cloud environments, this approach proposes an A3C-based real-time scheduler. They capture multiple  host and task parameters using the R2N2 architecture, along with temporal pat- terns, to produce efficient scheduling decisions. In this paper we propose a container allocation algorithm which incorporates energy efficient and bin packing heuristics to improve the energy efficiency of the model. Medium-Fit (MF), bin packing heuristics is incorporated to reduce SLA violation. Through experiments in CloudSim we compared the performance of both the model. The results indicate that the power efficient based model showed an improvement of 67% in terms of energy consumption
## Contributions



## Quick setup and run tutorial
Clone the repository and open the DLF folder in Idea IntelliJ IDE to execute the project.
1. In the terminal go the class *PEBFDLR.java/* inside the package package org.cloudbus.cloudsim.examples.power.DeepRL.

2. Select the baseline selection and placement algorithm to run the baseline model.
```
String conatinerAllocationPolicy =  "lr"; // Local Regression (LR) Conatiner allocation policy
String conatinerSelectionPolicy = "mmt"; // Minimum Migration Time (MMT) Conatiner selection policy
```
3. Run the *PEBFDLR.java/* class.

4. The baseline model starts running by creating conatiners and the stimulation interval is divided set of interval and each interval is 5 mins long. During each interval the loss is computed and the model is defined as optimal solution to reduce the overall loss.

<p>&nbsp;</p>
<div align="center">
<img src="https://github.com/Swarnavalli-Srinivasan/Efficient_Resource_Allocation_Serverless_Container/blob/main/DLSF/Results/Baseline_model_op.png" width="600" align="middle">
</div>

<p>&nbsp;</p>

5. Copy the output file *DL.txt* present parallel to DLF folder to *\DLSF\Models\LR-MTT* 

6. This output file will be used to create perfomance combination '.pickle' by the python file *Results/saveData.py*. The pickle files are stored in the path *\DLSF\Results\Data\LR-MTT*

7. Select the improvement model selection and placement algorithm to run the proposed powe efficieny improvement model.
```
String conatinerAllocationPolicy =  "PEBFDlr"; // Power efficiency Local Regression (LR) Conatiner allocation policy
String conatinerSelectionPolicy = "mmt"; // Minimum Migration Time (MMT) Conatiner selection policy
```
8. Run the *PEBFDLR.java/* class.

9. The model starts running by creating conatiners and the stimulation interval is divided set of interval and each interval is 5 mins long. During each interval the loss is computed and the model is defined as optimal solution to reduce the overall loss.

<p>&nbsp;</p>
<div align="center">
<img src="https://github.com/Swarnavalli-Srinivasan/Efficient_Resource_Allocation_Serverless_Container/blob/main/DLSF/Results/Proposed_model_op.png" width="600" align="middle">
</div>

<p>&nbsp;</p>

10. Copy the output file *DL.txt* present parallel to DLF folder to *\DLSF\Models\PEBFDLR-MTT* 

11. This output file will be used to create perfomance combination '.pickle' by the python file *Results/saveData.py*. The pickle files are stored in the path *\DLSF\Results\Data\PEBFDLR-MTT*

12. The benchmark graphs will be generated using *Results/resultGen.py* on the pickle file. 

13. The benchamrk graphs will be stored in *\DLSF*

## Benchmark

After running above steps, we can see benchmark as below with some graphs.

1.Comparision of energy efficiency between the baseline model and proposed improvement model.

<p>&nbsp;</p>
<p>&nbsp;</p>
<div align="center">
<img src="https://github.com/Swarnavalli-Srinivasan/Efficient_Resource_Allocation_Serverless_Container/blob/main/DLSF/Results/Toatl_energy_consumed.png" width="400" align="middle">
</div>

<p>&nbsp;</p> 
<p>&nbsp;</p>

2.Comparision of number of SLA violation between the baseline model and proposed improvement model.
<p>&nbsp;</p>
<p>&nbsp;</p>

<div align="center">
<img src="https://github.com/Swarnavalli-Srinivasan/Efficient_Resource_Allocation_Serverless_Container/blob/main/DLSF/Results/No_of_SLA_Violation.png" width="400" align="middle">
</div>
<p>&nbsp;</p>
<p>&nbsp;</p>

3.Comparision of response time between the baseline model and proposed improvement model.

<p>&nbsp;</p>
<p>&nbsp;</p>
<div align="center">
<img src="https://github.com/Swarnavalli-Srinivasan/Efficient_Resource_Allocation_Serverless_Container/blob/main/DLSF/Results/Response_time.png" width="400" align="middle">
</div>

<p>&nbsp;</p>
<p>&nbsp;</p>

4.Comparision of average time taken to complete a task between the baseline model and proposed improvement model.
<p>&nbsp;</p>
<p>&nbsp;</p>
<div align="center">
<img src="https://github.com/Swarnavalli-Srinivasan/Efficient_Resource_Allocation_Serverless_Container/blob/main/DLSF/Results/Average_time_to_complete_a_task.png" width="400" align="middle">
</div>

## Developer

[Swarnavalli Srinivasan](https://www.github.com/Swarnavalli-Srinivasan) (swarnavalli.srinivasan@gmail.com)
[Divya SureshKumar Thangam ](https://www.github.com/DivyaSureshkumarthangam) (divyast.18@gmail.com)


