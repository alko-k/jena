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

import com.hp.hpl.jena.sparql.expr.Expr ;
import com.hp.hpl.jena.sparql.expr.ExprList ;

public class AggMax extends AggMaxBase
{
    // ---- MAX(expr)
    public AggMax(Expr expr) { super(expr, false) ; } 
    @Override
    public Aggregator copy(ExprList expr) { return new AggMax(expr.get(0)) ; }

    @Override
    public int hashCode()   { return HC_AggMax ^ getExpr().hashCode() ; }
    
    @Override
    public boolean equals(Object other)
    {
        if ( this == other ) return true ; 
        if ( ! ( other instanceof AggMax ) )
            return false ;
        AggMax agg = (AggMax)other ;
        return Lib.equal(exprList, agg.exprList) ;
    }
}
