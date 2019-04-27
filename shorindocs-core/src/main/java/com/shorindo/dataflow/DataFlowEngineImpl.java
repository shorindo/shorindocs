/*
 * Copyright 2019 Shorindo, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shorindo.dataflow;

import java.io.InputStream;

import org.springframework.stereotype.Component;

/**
 * 
 */
@Component
public class DataFlowEngineImpl implements DataFlowEngine {

    /* (non-Javadoc)
     * @see com.shorindo.dataflow.DataFlowEngine#load(java.io.InputStream)
     */
    @Override
    public void load(InputStream is) throws DataFlowException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.shorindo.dataflow.DataFlowEngine#input(java.lang.String, java.lang.Object)
     */
    @Override
    public Object input(String processId, Object data) throws DataFlowException {
        // TODO Auto-generated method stub
        return null;
    }

}
