package dev ;

public class DevTDB
{
    // == 0.8.5
    // Enable FILTER assignment for strings (and numbers?) via dataset context setting?
    // IRI checking - sort out error and warnings.
    // Loading from an InputStream (other than stdin) 
    // No checking loading.
    // tdbloader from quads?
    
    // Tuples
    // Interface, TupleFactory, TupleImpl
    // TupleMask (or TupleImpl itself has a length field?? TupleMask(T[] or Tuple<T>)) 
    // Sort : with colmap?
    // TupleSlice
    
    // -- Loader
    // .gz files.
    // Sink: just a send()
    // Destination start-send-finish
    // SinkClosable
    // Consolidate all event types.
    // Test bulk loader
    
    // DBPedia load
    
    // RIOT
    //  Option passing (old Jena style).
    //  Options: pass/reject bad URIs, bad literals. -> "SKIP", "PASS", "FAIL"
    //  Exposing quads
    
    // Testing (W64)
    // Build-release
    // tdbverifyTDB
    // ----
    
    // Free block recycling.
    
    // 2 store dataset
    // Compact B+Trees
    // TDB - wrapper version
    //   Uses RIOT for I/O
    // ----
    
    // ---- Core system

    // ---- Pipeline
    // Weak inferencing (in query rewriting):
    // owl:equivalentClass, owl:equivalentProperty,
    // owl:inverseOf, owl:SymmetricProperty, owl:TransitiveProperty
    // rdfs:subClassOf (aux table).
    // rdfs:subPropertyOf (aux table).
    
    
    // Stats and datasets
    //   tdbstats
    
    // Use  $(cygpath -wp "$jar")" when setting $jar.
    
    // RIOT
    //     NQuads in Trig ??
    //     Trailing dots ??
    // Document
    
    // --------

    // Early truncation of patterns
    //  ?s <p> <foo> . ?s <q> ?v . ?s <r> ?x
    // If <r> has no solutions fall back to triple pattern 1
    // Just need to keep var -> first def mapping but all mentions may be useful. 
    // (Idea from Alisdair)
    
    // In-JVM caches - make a function of heap size.
    // Settable in this.info.
    //   NodeCache
    //   Block cache for 32 bit
    //   Run with 32 bit block cache on 64 bit large machine to measure difference.

    // ** Advanced block work - free chain management.
    // Where does the head of the free chain go?  In .info? c.f. moving root. In root link field?!
    //   Separate control file (allows swicth bewteen two very different block files)
    //   Header block (a bit big!)
    //   Virtual to physical block system. **
    // Block* is currently physical blocks.
    // VBlocks.
    
    // Negative count? No 
    // Block is currently (B+Tree block)
    //  final public static int COUNT      = 0 ;
    //  final public static int LINK       = 4 ;
    // HashBucket
    //   final public static int TRIE        = COUNT+4 ;
    //   final public static int BITLEN      = TRIE+4 ;
    // On-disk: first 4 bytes is type << 24 | count
    //   ?? Allocate a type for a free block.
    //   ?? Maintain a free block disk
    //   ==> recordfile level.
    
    
    // Special cases
    //   <s> p1 ?o1 ; p2 ?o2 ; p3 ?o3 ... and do ((<s> 0 0)->(<s>+1 0 0)]
    //   Materialized answers.
    
    // Dataset.close() always calls TDBMaker.releaseDataset - shouldn't there be a reference count?
    
    // NodeId:  
    // Bit 0: 0 - 63 bits of id (hash!) or block allocation for cluster.
    // Bit 0: 1 - inline
    // Schema compatibility needs to handle this carefully.
    
    // Case canonicalized lang tags? Affects hashing.
    
    // NodeTableFactory and SetupTDB.makeNodeTable have common code. 
    //   Remove NodeTableFactory and have one per-technology setup/maker
    
    // ** Grand roll out
    //    atlas to atlas
    //    riot to ARQ, PrrefixMapping=>PrefixMap, Prologue change.
    //    FmtUtils and NodeFmtLib 
    
    // Sort out NodecSSE and NodecLib
    
    // **** Redo IndexBuilder and NodeTableBuilder (with caching
    //   ?? SetupTDB(IndexBuilder, NodeTableBuilder, PrefixTableBuilder) ;
    
    // === Projects
    // -> Stopping long running queries - 
    //    hook in BGP/Quad patterns
    // -> BDB-JE & transactions
    // -> BDB-JE and compressed blocks.
    
    // Sort out DatasetGraphMakerTDB -> One type, not thing+mem.
    //   Remove FactoryGraphTDB
    //   IndexMakers?
    // ?? DatasetGraphSetupMem == TDBMakerFactoryGraphMem

    // ----
    // B+Tree checking utility.
    // Dataset checking utility.

    // IndexFactory understanding index type name
    //    Registry<String->T>("bplustree", IndexBuilder)
    
    // == Misc
    // Node cache on 64bit machines needs to be bigger or rebalence
    // Cache stats counters (prep for JMX but useful now)
    
    // == tdbdump && tdbrestore
    // ---- Optimizer
    
    // ---- Documentation
    
    // ---- Misc
    // Inlines => Inline56, Inline64, ??
}
