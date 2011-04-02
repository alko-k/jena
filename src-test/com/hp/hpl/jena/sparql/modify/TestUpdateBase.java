/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sparql.modify;

import junit.framework.TestCase ;

import com.hp.hpl.jena.graph.Graph ;
import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.graph.Triple ;
import com.hp.hpl.jena.sparql.util.graph.GraphFactory ;
import com.hp.hpl.jena.update.GraphStore ;
import com.hp.hpl.jena.update.UpdateAction ;

public abstract class TestUpdateBase extends TestCase
{
    protected abstract GraphStore getEmptyGraphStore() ; 
    
    protected void defaultGraphData(GraphStore gStore, Graph data)
    {
        Graph g = gStore.getDefaultGraph() ;
        g.getBulkUpdateHandler().removeAll() ;
        g.getBulkUpdateHandler().add(data) ;
    }
    
    protected void namedGraphData(GraphStore gStore, Node uri, Graph data)
    {
        Graph g = gStore.getGraph(uri) ;
        if ( g == null )
        {
            gStore.addGraph(uri, GraphFactory.createJenaDefaultGraph()) ;
            g = gStore.getGraph(uri) ;
        }
        else
            g.getBulkUpdateHandler().removeAll() ;
        g.getBulkUpdateHandler().add(data) ;
    }
    
    protected static final String FileBase = TS_Update.testDirUpdate ;
    
    protected static void script(GraphStore gStore, String filename)
    {
        UpdateAction.readExecute(FileBase+"/"+filename, gStore) ;
    }
    
    protected static boolean graphEmpty(Graph graph)
    {
        return graph.isEmpty() ; 
    }
    
    protected static boolean graphContains(Graph graph, Triple triple)
    {
        return graph.contains(triple) ; 
    }
}

/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */