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
package org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.curricularRules.ICurricularRule;
import org.fenixedu.academic.domain.curricularRules.MaximumNumberOfCreditsForEnrolmentPeriod;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;

public class MaximumNumberOfCreditsForEnrolmentPeriodExecutor extends CurricularRuleExecutor {

    @Override
    protected RuleResult executeEnrolmentVerificationWithRules(final ICurricularRule curricularRule,
            IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate, final EnrolmentContext enrolmentContext) {

        final ExecutionSemester executionSemester = enrolmentContext.getExecutionPeriod();

        double accumulated = 0d;
        for (final IDegreeModuleToEvaluate degreeModuleToEvaluate : enrolmentContext.getDegreeModulesToEvaluate()) {
            accumulated += calculateAccumulatedEctsCredits(degreeModuleToEvaluate, enrolmentContext);
        }

        final double maxEcts =
                MaximumNumberOfCreditsForEnrolmentPeriod.getMaximumNumberOfCredits(enrolmentContext.getStudentCurricularPlan(),
                        executionSemester.getExecutionYear());

        if (accumulated > maxEcts) {
            if (sourceDegreeModuleToEvaluate.isEnroled()) {
                return RuleResult.createImpossible(sourceDegreeModuleToEvaluate.getDegreeModule(),
                        "curricularRules.ruleExecutors.MaximumNumberOfCreditsForEnrolmentPeriodExecutor",
                        String.valueOf(maxEcts), String.valueOf(accumulated));

            } else {
                return RuleResult.createFalse(sourceDegreeModuleToEvaluate.getDegreeModule(),
                        "curricularRules.ruleExecutors.MaximumNumberOfCreditsForEnrolmentPeriodExecutor",
                        String.valueOf(maxEcts), String.valueOf(accumulated));
            }
        }

        return RuleResult.createTrue(sourceDegreeModuleToEvaluate.getDegreeModule());
    }

    /*
     * Accumulated ECTS as domain logic is a legacy behavior and this kind of specific methods should not live on domain space, 
     * they should be specific to curricular rules to allow greater flexibility / specialization by institutions.
     * 
     * The current implementation of accumulation factor is hardcoded and set to 1, check MaximumNumberOfCreditsForEnrolmentPeriod rule for details.
     * 
     * New code should not depend on domain provided methods for this purpose.
     * 
     */
    private double calculateAccumulatedEctsCredits(final IDegreeModuleToEvaluate degreeModuleToEvaluate,
            EnrolmentContext enrolmentContext) {

        if (!degreeModuleToEvaluate.isLeaf()) {
            return 0d;
        }

        if (enrolmentContext.isToEvaluateRulesByYear()) {
            return degreeModuleToEvaluate.getEctsCredits();
        }

        // method call is kept because it returns the ects share for a specific semester (keep this in mind when refactoring to remove accumulated ects credits logic)
        return degreeModuleToEvaluate.getAccumulatedEctsCredits(enrolmentContext.getExecutionPeriod());
    }

    @Override
    protected RuleResult executeEnrolmentWithRulesAndTemporaryEnrolment(final ICurricularRule curricularRule,
            final IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate, final EnrolmentContext enrolmentContext) {
        return executeEnrolmentVerificationWithRules(curricularRule, sourceDegreeModuleToEvaluate, enrolmentContext);
    }

    @Override
    protected RuleResult executeEnrolmentInEnrolmentEvaluation(final ICurricularRule curricularRule,
            final IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate, final EnrolmentContext enrolmentContext) {
        return RuleResult.createFalse(sourceDegreeModuleToEvaluate.getDegreeModule());
    }

    @Override
    protected boolean canBeEvaluated(ICurricularRule curricularRule, IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate,
            EnrolmentContext enrolmentContext) {
        return true;
    }

}
