/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hp.hpl.jena.sparql.expr.aggregate;

import org.apache.jena.atlas.lib.Lib ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.engine.binding.Binding ;
import com.hp.hpl.jena.sparql.expr.Expr ;
import com.hp.hpl.jena.sparql.expr.ExprList ;
import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.function.FunctionEnv ;

public class AggSample extends AggregatorBase
{
    // ---- Sample(expr)
    public AggSample(Expr expr) { super("SAMPLE", false, expr) ; } 
    
    @Override
    public Aggregator copy(ExprList exprs) { return new AggSample(exprs.get(0)) ; }
    
    @Override
    public Accumulator createAccumulator()
    { 
        return new AccSample(getExpr()) ;
    }

    @Override
    public Node getValueEmpty()     { return null ; } 

    @Override
    public int hashCode()   { return HC_AggSample ^ getExpr().hashCode() ; }
    @Override
    public boolean equals(Object other)
    {
        if ( this == other ) return true ; 
        if ( ! ( other instanceof AggSample ) )
            return false ;
        AggSample agg = (AggSample)other ;
        return Lib.equal(this.exprList, agg.exprList) ;
    } 

    // ---- Accumulator
    private static class AccSample extends AccumulatorExpr
    {
        // Sample: first evaluation of the expression that is not an error.
        private NodeValue sampleSoFar = null ;

        public AccSample(Expr expr) { super(expr) ; }

        @Override
        public void accumulate(NodeValue nv , Binding binding, FunctionEnv functionEnv)
        { 
            if ( sampleSoFar == null )
            {
                sampleSoFar = nv ;
                return ;
            }
        }

        @Override
        protected void accumulateError(Binding binding, FunctionEnv functionEnv)
        {}
        
        @Override
        public NodeValue getAccValue()
        { return sampleSoFar ; }
    }
}
