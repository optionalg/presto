/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.sql.planner.iterative;

import com.facebook.presto.sql.planner.plan.AggregationNode;
import com.facebook.presto.sql.planner.plan.AssignUniqueId;
import com.facebook.presto.sql.planner.plan.DistinctLimitNode;
import com.facebook.presto.sql.planner.plan.EnforceSingleRowNode;
import com.facebook.presto.sql.planner.plan.ExceptNode;
import com.facebook.presto.sql.planner.plan.FilterNode;
import com.facebook.presto.sql.planner.plan.IntersectNode;
import com.facebook.presto.sql.planner.plan.LimitNode;
import com.facebook.presto.sql.planner.plan.PlanNode;
import com.facebook.presto.sql.planner.plan.PlanVisitor;
import com.facebook.presto.sql.planner.plan.ProjectNode;
import com.facebook.presto.sql.planner.plan.TopNNode;
import com.facebook.presto.sql.planner.plan.ValuesNode;

public final class DistinctOutputQueryUtil
{
    private DistinctOutputQueryUtil() {}

    // TODO: This class can be removed when traits are implemented and nodes can have a distinct trait
    public static boolean isDistinct(PlanNode node, Lookup lookup)
    {
        return node.accept(new IsDistinctPlanVisitor(lookup), null);
    }

    private static final class IsDistinctPlanVisitor
            extends PlanVisitor<Void, Boolean>
    {
        private final Lookup lookup;

        public IsDistinctPlanVisitor(Lookup lookup)
        {
            this.lookup = lookup;
        }

        @Override
        protected Boolean visitPlan(PlanNode node, Void context)
        {
            return false;
        }

        @Override
        public Boolean visitAggregation(AggregationNode node, Void context)
        {
            return true;
        }

        @Override
        public Boolean visitAssignUniqueId(AssignUniqueId node, Void context)
        {
            return true;
        }

        @Override
        public Boolean visitDistinctLimit(DistinctLimitNode node, Void context)
        {
            return true;
        }

        @Override
        public Boolean visitEnforceSingleRow(EnforceSingleRowNode node, Void context)
        {
            return true;
        }

        @Override
        public Boolean visitExcept(ExceptNode node, Void context)
        {
            return true;
        }

        @Override
        public Boolean visitFilter(FilterNode node, Void context)
        {
            return lookup.resolve(node.getSource()).accept(this, null);
        }

        @Override
        public Boolean visitIntersect(IntersectNode node, Void context)
        {
            return true;
        }

        @Override
        public Boolean visitProject(ProjectNode node, Void context)
        {
            return node.isIdentity() && lookup.resolve(node.getSource()).accept(this, null);
        }

        @Override
        public Boolean visitValues(ValuesNode node, Void context)
        {
            return node.getRows().size() == 1;
        }

        @Override
        public Boolean visitLimit(LimitNode node, Void context)
        {
            return node.getCount() <= 1;
        }

        @Override
        public Boolean visitTopN(TopNNode node, Void context)
        {
            return node.getCount() <= 1;
        }
    }
}