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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.log.ScmLogger;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * @author <a href="mailto:vincent@latombe.net">Vincent Latombe</a>
 */
public class GitPushConsumer
    implements StreamConsumer
{

    /**
     * The pattern used to match a successfully pushed fast-forward ref line
     */
    private static final Pattern FASTFORWARD_PATTERN = Pattern.compile( "^\\t([^\\t]*):([^\\t]*)\\t(.*)\\((.*)\\)$" );

    /**
     * The pattern used to match a successfully forced update ref line
     */
    private static final Pattern FORCED_PATTERN = Pattern.compile( "^\\+\\t([^\\t]*):([^\\t]*)\\t(.*)\\((.*)\\)$" );

    /**
     * The pattern used to match deleted file lines
     */
    private static final Pattern NEWREF_PATTERN = Pattern.compile( "^\\*\\t([^\\t]*):([^\\t]*)\\t(.*)\\((.*)\\)$" );

    /**
     * The pattern used to match renamed file lines
     */
    private static final Pattern REJECTED_PATTERN = Pattern.compile( "^\\!\\t([^\\t]*):([^\\t]*)\\t(.*)\\((.*)\\)$" );

    private ScmLogger logger;

    /**
     * SCM Branches that have been pushed
     */
    private List<ScmBranch> pushedBranches = new ArrayList<ScmBranch>();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Consumer when workingDirectory and repositoryRootDirectory are the same
     * 
     * @param logger the logger
     * @param workingDirectory the working directory
     */
    public GitPushConsumer( ScmLogger logger )
    {
        this.logger = logger;
    }

    // ----------------------------------------------------------------------
    // StreamConsumer Implementation
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void consumeLine( String line )
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( line );
        }
        if ( StringUtils.isEmpty( line ) )
        {
            return;
        }

        Matcher matcher;
        if ( ( matcher = FASTFORWARD_PATTERN.matcher( line ) ).find() )
        {
            pushedBranches.add(new ScmBranch(matcher.group(1)));
        }
        else if ( ( matcher = FORCED_PATTERN.matcher( line ) ).find() )
        {
            pushedBranches.add(new ScmBranch(matcher.group(1)));
        }
        else if ( ( matcher = NEWREF_PATTERN.matcher( line ) ) .find() )
        {
          pushedBranches.add(new ScmBranch(matcher.group(1)));
        }
        else if ( ( matcher = REJECTED_PATTERN.matcher( line ) ) .find() )
        {
          logger.error("Some ref push has been rejected : " + matcher.group(0));
          return;
        }
        else
        {
        	logger.warn( "Ignoring unrecognized line: " +  line );
        	return;
        }
    }

    public List<ScmBranch> getPushedBranches()
    {
        return pushedBranches;
    }
}
