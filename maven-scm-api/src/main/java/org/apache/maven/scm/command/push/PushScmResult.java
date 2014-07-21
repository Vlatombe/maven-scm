package org.apache.maven.scm.command.push;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.List;

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmResult;

/**
 * @author <a href="mailto:vincent@latombe.net">Vincent Latombe</a>
 *
 */
public class PushScmResult
    extends ScmResult
{

    private List<ScmBranch> pushedBranches;

    private static final long serialVersionUID = 6687781645894401158L;

    public PushScmResult( String commandLine, String providerMessage, String commandOutput, boolean success )
    {
        super( commandLine, providerMessage, commandOutput, success );
    }

    public PushScmResult( String commandLine, List<ScmBranch> pushedBranches )
    {
        super( commandLine, null, null, true );
        
        this.pushedBranches = pushedBranches;
    }

    public PushScmResult( ScmResult result )
    {
        super( result );
    }
    
    public List<ScmBranch> getPushedBranches()
    {
        return pushedBranches;
    }
}
