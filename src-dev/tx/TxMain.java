/*
 * (c) Copyright 2011 Epimorphics Ltd.
 * All rights reserved.
 * [See end of file]
 */

package tx;

import java.util.Iterator ;

import org.openjena.atlas.lib.Bytes ;
import org.openjena.atlas.lib.FileOps ;
import org.openjena.atlas.logging.Log ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import tx.other.BlockMgrTracker ;
import tx.transaction.TransactionManager ;

import com.hp.hpl.jena.query.DatasetFactory ;
import com.hp.hpl.jena.query.Query ;
import com.hp.hpl.jena.query.QueryExecution ;
import com.hp.hpl.jena.query.QueryExecutionFactory ;
import com.hp.hpl.jena.query.QueryFactory ;
import com.hp.hpl.jena.query.Syntax ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.sse.SSE ;
import com.hp.hpl.jena.sparql.util.QueryExecUtils ;
import com.hp.hpl.jena.tdb.base.block.BlockMgr ;
import com.hp.hpl.jena.tdb.base.block.BlockMgrFactory ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.base.record.Record ;
import com.hp.hpl.jena.tdb.base.record.RecordFactory ;
import com.hp.hpl.jena.tdb.index.RangeIndex ;
import com.hp.hpl.jena.tdb.index.RangeIndexLogger ;
import com.hp.hpl.jena.tdb.index.bplustree.BPlusTree ;
import com.hp.hpl.jena.tdb.index.bplustree.BPlusTreeParams ;
import com.hp.hpl.jena.tdb.index.btree.BTree ;
import com.hp.hpl.jena.tdb.index.btree.BTreeParams ;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB ;
import com.hp.hpl.jena.update.UpdateAction ;
import com.hp.hpl.jena.update.UpdateFactory ;
import com.hp.hpl.jena.update.UpdateRequest ;

/* NEXT
 * Setup
 * 
 *   allocateId - allocateBuffer combined
 *   freeBlock inc contents. freeBlock(id, byteBuffer)
 *   Do we need a Block class?  Page class?
 *     
 */

public class TxMain
{
    /*
     * Reads not doing a release
     * Iterator tracking
     * End transaction => close all open iterators.
     *   BPlusTree.replicate(BlockMgr1, BlocMgr2)
     * Recycle DatasetGraphTx objects.  Setup - set PageView
     * 
     * Allocate and deallocate "Blocks" = id, (raw) size, and ByteBuffer (which knows it's size?)
     *   Build for variable length
     */
    
    static { Log.setLog4j() ; }

    public static void main(String... args)
    {
        String dirname = "DBX" ;
        if ( false && FileOps.exists(dirname) )
            FileOps.clearDirectory(dirname) ;
        TransactionManager txnMgr = new TransactionManager() ;
        
        //Location location = Location.mem() ;
        Location location = new Location(dirname) ;
        
        DatasetGraphTDB dsg = txnMgr.build(location) ;
        //dsg.add(SSE.parseQuad("(_ <s> <p> 'o')")) ;
        
        DatasetGraphTxView dsgX1 = txnMgr.begin(dsg) ;
        dsgX1.add(SSE.parseQuad("(_ <sx> <px> 'ox1')")) ;
        
//        System.out.println("Base:") ;
//        //System.out.println(dsg) ;
//        query("SELECT count(*) { ?s ?p ?o }", dsg) ;
        
        System.out.println("Transaction:") ;
        //System.out.println(dsgX) ;
        query("SELECT count(*) { ?s ?p ?o }", dsgX1) ;
        update("CLEAR DEFAULT", dsgX1) ;
        query("SELECT count(*) { ?s ?p ?o }", dsgX1) ;
        
        System.out.println("Base:") ;
        //System.out.println(dsg) ;
        query("SELECT count(*) { ?s ?p ?o }", dsg) ;
        
        
        DatasetGraphTxView dsgX2 = txnMgr.begin(dsg) ;
        dsgX2.add(SSE.parseQuad("(_ <sx> <px> 'ox2')")) ;

        
        System.out.println("Transaction:") ;
        //System.out.println(dsgX) ;
        query("SELECT count(*) { ?s ?p ?o }", dsgX1) ;
        dsgX1.abort() ;
        
        System.out.println("Done") ;
        System.exit(0) ;
        
    }
    
    public static void query(String queryStr, DatasetGraph dsg)
    {
        Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ) ;
        QueryExecution qExec = QueryExecutionFactory.create(query, DatasetFactory.create(dsg)) ;
        QueryExecUtils.executeQuery(query, qExec) ;
    }
    
    public static void update(String updateStr, DatasetGraph dsg)
    {
        UpdateRequest req = UpdateFactory.create(updateStr) ;
        UpdateAction.execute(req, dsg) ;
    }
    
    public static void bpTreeTracking(String... args)
    {
        RecordFactory rf = new RecordFactory(8,8) ;
        
        RangeIndex rIndex ;
        String label ;
        if ( true )
        {
            label = "B+Tree" ;
            int order = 2 ;
            
            BPlusTreeParams params = new BPlusTreeParams(order, rf) ;
            System.out.println(label+": "+params) ;
            
            int blockSize  = BPlusTreeParams.calcBlockSize(order, rf) ;
            System.out.println("Block size = "+blockSize) ;
            
            BlockMgr mgr1 = BlockMgrFactory.createMem("B1", blockSize) ;
            mgr1 = new BlockMgrTracker("BlkMgr1", mgr1) ;
            
            BlockMgr mgr2 = BlockMgrFactory.createMem("B2", blockSize) ;
            mgr2 = new BlockMgrTracker("BlkMgr2", mgr2) ;
            BPlusTree bpt = BPlusTree.attach(params, mgr1, mgr2) ;
            rIndex = bpt ;
        }
        else
        {
            label = "BTree" ;
            int btOrder  = 3 ;
            int blkSize = BTreeParams.calcBlockSize(btOrder, rf) ;
            BlockMgr mgr = BlockMgrFactory.createMem("B3", blkSize) ;
            mgr = new BlockMgrTracker("B3", mgr) ;
            BTreeParams params = new BTreeParams(btOrder, rf) ;
            System.out.println(label+": "+params) ;
            BTree btree = new BTree(params, mgr) ;
            rIndex = btree ;
        }
        
        final Logger log = LoggerFactory.getLogger(label) ;
        
        // Add logging.
        rIndex = new RangeIndexLogger(rIndex, log) ;
        
        for ( int i = 0 ; i < 4 ; i++ ) 
        {
            Record r = record(rf, i+0x100000L, i+0x90000000L) ;
            rIndex.add(r) ;
        }

        System.out.println() ;
        
        Record r = record(rf, 3+0x100000L, 0) ;
        r = rIndex.find(r) ;
        System.out.println() ;
        
        Iterator<Record> iter = rIndex.iterator() ;
        for ( ; iter.hasNext() ; )
            System.out.println(iter.next()) ;
        System.out.println() ;

//        bpt.dump() ;
    }

    static Record record(RecordFactory rf, long key, long val)
    {
        Record r = rf.create() ;
        Bytes.setLong(key, r.getKey()) ;
        Bytes.setLong(val, r.getValue()) ;
        return r ;
    }
}

/*
 * (c) Copyright 2011 Epimorphics Ltd.
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