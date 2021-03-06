/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hama.bsp.sync;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hama.bsp.BSPJobID;
import org.apache.hama.bsp.TaskAttemptID;

public abstract class BSPPeerSyncClient implements PeerSyncClient {

  /**
   * Init will be called within a spawned task, it should be used to initialize
   * the inner structure and fields, e.G. a zookeeper client or an rpc
   * connection to the real sync daemon.
   * 
   * @throws Exception
   */
  public abstract void init(Configuration conf, BSPJobID jobId,
      TaskAttemptID taskId) throws Exception;

  /**
   * Enters the barrier before the message sending in each superstep.
   * 
   * @param jobId the jobs ID
   * @param taskId the tasks ID
   * @param superstep the superstep of the task
   * @throws SyncException
   */
  public abstract void enterBarrier(BSPJobID jobId, TaskAttemptID taskId,
      long superstep) throws SyncException;

  /**
   * Leaves the barrier after all communication has been done, this is usually
   * the end of a superstep.
   * 
   * @param jobId the jobs ID
   * @param taskId the tasks ID
   * @param superstep the superstep of the task
   * @throws SyncException
   */
  public abstract void leaveBarrier(BSPJobID jobId, TaskAttemptID taskId,
      long superstep) throws SyncException;

  /**
   * Registers a specific task with a its host and port to the sync daemon.
   * 
   * @param jobId the jobs ID
   * @param taskId the tasks ID
   * @param hostAddress the host where the sync server resides
   * @param port the port where the sync server is up
   */
  public abstract void register(BSPJobID jobId, TaskAttemptID taskId,
      String hostAddress, long port);

  /**
   * Returns all registered tasks within the sync daemon. They have to be
   * ordered ascending by their task id.
   * 
   * @param jobID the job ID
   * @return an <b>ordered</b> string array of host:port pairs of all tasks
   *         connected to the daemon.
   */
  public abstract String[] getAllPeerNames(BSPJobID jobID);

  /**
   * TODO this has currently no use. Could later be used to deregister tasks
   * from the barrier during runtime if they are finished. Something equal to
   * voteToHalt() in Pregel.
   * 
   * @param jobId
   * @param taskId
   * @param hostAddress
   * @param port
   */
  public abstract void deregisterFromBarrier(BSPJobID jobId,
      TaskAttemptID taskId, String hostAddress, long port);

  /**
   * This stops the sync daemon. Only used in YARN.
   */
  public abstract void stopServer();

  /**
   * This method should close all used resources, e.G. a ZooKeeper instance.
   * 
   * @throws InterruptedException
   */
  public abstract void close() throws IOException;

}
