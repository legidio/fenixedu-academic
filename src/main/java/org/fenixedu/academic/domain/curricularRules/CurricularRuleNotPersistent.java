/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.domain.curricularRules;

import java.util.function.Supplier;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleExecutor;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleExecutorFactory;
import org.fenixedu.academic.domain.curricularRules.executors.verifyExecutors.VerifyRuleLevel;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.joda.time.YearMonthDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class CurricularRuleNotPersistent implements ICurricularRule {

    static private final Logger logger = LoggerFactory.getLogger(CurricularRuleNotPersistent.class);

    @Override
    public boolean equals(Object obj) {
        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        CurricularRuleNotPersistent curricularRuleNotPersistent = null;
        if (obj instanceof CurricularRuleNotPersistent) {
            curricularRuleNotPersistent = (CurricularRuleNotPersistent) obj;
        } else {
            return false;
        }

        return this.getDegreeModuleToApplyRule() == curricularRuleNotPersistent.getDegreeModuleToApplyRule()
                && this.getCurricularRuleType() == curricularRuleNotPersistent.getCurricularRuleType();
    }

    @Override
    public int hashCode() {
        final StringBuilder builder = new StringBuilder();
        if (getDegreeModuleToApplyRule() != null) {
            builder.append(String.valueOf(getDegreeModuleToApplyRule().hashCode()));
            builder.append('@');
        }
        builder.append(String.valueOf(getCurricularRuleType().hashCode()));
        return builder.toString().hashCode();
    }

    @Override
    public boolean appliesToContext(Context context) {
        return context == null || this.appliesToCourseGroup(context.getParentCourseGroup());
    }

    @Override
    public boolean appliesToCourseGroup(CourseGroup courseGroup) {
        return (this.getContextCourseGroup() == null || this.getContextCourseGroup() == courseGroup);
    }

    @Override
    public boolean hasContextCourseGroup() {
        return getContextCourseGroup() != null;
    }

    @Override
    public boolean hasCurricularRuleType(CurricularRuleType ruleType) {
        return getCurricularRuleType() == ruleType;
    }

    @Override
    public boolean isCompositeRule() {
        return getCurricularRuleType() == null;
    }

    @Override
    public boolean isValid(ExecutionSemester executionSemester) {
        return (getBegin().isBeforeOrEquals(executionSemester)
                && (getEnd() == null || getEnd().isAfterOrEquals(executionSemester)));
    }

    @Override
    public boolean isValid(ExecutionYear executionYear) {
        for (ExecutionSemester executionSemester : executionYear.getExecutionPeriodsSet()) {
            if (isValid(executionSemester)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean isActive() {
        return getEnd() == null || getEnd().containsDay(new YearMonthDay());
    }

    static private Supplier<CurricularRuleExecutorFinder> CURRICULAR_RULE_EXECUTOR_FINDER =
            () -> new CurricularRuleExecutorFinder() {

                @Override
                public CurricularRuleExecutor find(final ICurricularRule input) {
                    return CurricularRuleExecutorFactory.findExecutor(input);
                }
            };

    @Override
    public CurricularRuleExecutorFinder getCurricularRuleExecutorFinder() {
        return CURRICULAR_RULE_EXECUTOR_FINDER.get();
    }

    static public void setCurricularRuleExecutorFinder(final Supplier<CurricularRuleExecutorFinder> input) {
        if (input != null && input.get() != null) {
            CURRICULAR_RULE_EXECUTOR_FINDER = input;
        } else {
            logger.error("Could not set CURRICULAR_RULE_EXECUTOR_FINDER to null");
        }
    }

    @Override
    public RuleResult evaluate(final IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate, EnrolmentContext enrolmentContext) {

        return getCurricularRuleExecutorFinder().find(this).execute(this, sourceDegreeModuleToEvaluate, enrolmentContext);
    }

    @Override
    public RuleResult verify(final VerifyRuleLevel level, final EnrolmentContext enrolmentContext,
            final DegreeModule degreeModuleToVerify, final CourseGroup parentCourseGroup) {
        return createVerifyRuleExecutor().verify(this, level, enrolmentContext, degreeModuleToVerify, parentCourseGroup);
    }

}
