package org.apache.maven.scm.provider.git.gitexe.command.push;

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

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.push.AbstractPushCommand;
import org.apache.maven.scm.command.push.PushScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;
import org.apache.maven.scm.provider.git.gitexe.command.GitCommandLineUtils;
import org.apache.maven.scm.provider.git.gitexe.command.branch.GitBranchCommand;
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @author <a href="mailto:vincent@latombe.net">Vincent Latombe</a>
 *
 */
public class GitPushCommand
    extends AbstractPushCommand
    implements GitCommand
{
    
    /** {@inheritDoc} */
    public ScmResult executePushCommand( ScmProviderRepository repo, ScmFileSet fileSet, String tag )
        throws ScmException
    {
        if ( tag == null || StringUtils.isEmpty( tag.trim() ) )
        {
            throw new ScmException( "tag name must be specified" );
        }
        
        GitScmProviderRepository repository = (GitScmProviderRepository) repo;
        
        String branch = GitBranchCommand.getCurrentBranch( getLogger(), repository, fileSet );
        
        if ( branch == null || branch.length() == 0 )
        {
            throw new ScmException( "Could not detect the current branch. Don't know where I should push to!" );
        }


        CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

        int exitCode;

        if( repo.isPushChanges() ) 
        {
            // and now push the tag to the configured upstream repository
            
            Commandline clPush = createCommandLine( repository, fileSet.getBasedir(), branch, tag );
            GitPushConsumer pushConsumer = new GitPushConsumer( getLogger() );

            exitCode = GitCommandLineUtils.execute( clPush, pushConsumer, stderr, getLogger() );
            if ( exitCode != 0 )
            {
                return new PushScmResult( clPush.toString(), "The git-push command failed.", stderr.getOutput(), false );
            }
            else
            {
                return new PushScmResult( clPush.toString(), pushConsumer.getPushedBranches() );
            }
        }

        return new PushScmResult( "pushChanges is set to false. Skipping push.", null );

    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static Commandline createCommandLine( GitScmProviderRepository repository, File workingDirectory, String branch, String tag )
    {
        Commandline cl = GitCommandLineUtils.getBaseGitCommandLine( workingDirectory, "push" );
        cl.createArg().setValue( repository.getPushUrl() );
        // push the current branch
        cl.createArg().setValue( "refs/heads/" +  branch + ":refs/heads/" +  branch);
        // push the tag
        cl.createArg().setValue( "refs/tags/" + tag );

        return cl;
    }

}
