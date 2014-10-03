package org.apache.maven.scm.provider.git.jgit.command.push;


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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.push.AbstractPushCommand;
import org.apache.maven.scm.command.push.PushScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;
import org.apache.maven.scm.provider.git.jgit.command.JGitUtils;
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.RemoteRefUpdate.Status;

/**
 * @author <a href="mailto:vincent@latombe.net">Vincent Latombe</a>
 * 
 */
public class JGitPushCommand
    extends AbstractPushCommand
    implements GitCommand
{
  
    @Override
    protected ScmResult executePushCommand(ScmProviderRepository repo, ScmFileSet fileSet, String tag)
        throws ScmException {
      if ( tag == null || StringUtils.isEmpty( tag.trim() ) )
      {
          throw new ScmException( "tag name must be specified" );
      }
      Git git = null;
      try
      {
          File basedir = fileSet.getBasedir();
          git = Git.open( basedir );
          if ( repo.isPushChanges() )
          {
              String branch = git.getRepository().getBranch();
              RefSpec branchRefSpec = new RefSpec( Constants.R_HEADS + branch + ":" + Constants.R_HEADS + branch );
              RefSpec tagRefSpec = new RefSpec( Constants.R_TAGS + tag );
              getLogger().info( "push changes to remote... " + branchRefSpec.toString() + " " + tagRefSpec.toString());
              Iterable<PushResult> pushResults = JGitUtils.push( getLogger(), git, (GitScmProviderRepository) repo, branchRefSpec, tagRefSpec );
              List<ScmBranch> branches = new ArrayList<ScmBranch>();
              for (PushResult pushResult : pushResults) {
                for (RemoteRefUpdate remoteRefUpdate : pushResult.getRemoteUpdates()) {
                  if (Status.OK.equals(remoteRefUpdate.getStatus())) {
                    branches.add(new ScmBranch(remoteRefUpdate.getSrcRef()));
                  }
                }
              }
              return new PushScmResult( "JGit push", branches);
          }
      }
      catch ( Exception e )
      {
          throw new ScmException( "JGit push failure!", e );
      }
      finally
      {
          JGitUtils.closeRepo( git );
      }
      return new PushScmResult( "pushChanges is set to false. Skipping push.", null );
    }

}
