package net.sourceforge.fenixedu.presentationTier.Action.pedagogicalCouncil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.domain.ExecutionDegree;
import net.sourceforge.fenixedu.domain.ExecutionSemester;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.RootDomainObject;
import net.sourceforge.fenixedu.domain.Teacher;
import net.sourceforge.fenixedu.domain.TutorshipIntention;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.presentationTier.Action.base.FenixDispatchAction;

import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyConverter;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/viewTutors", module = "pedagogicalCouncil")
@Forwards({ @Forward(name = "viewTutors", path = "/pedagogicalCouncil/tutorship/viewTutors.jsp") })
public class ViewTutorsDA extends FenixDispatchAction {

    private static final Integer SEMESTER = 1;

    public ActionForward listTutors(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ViewTutorsBean bean = getRenderedObject("tutorsBean");
        if (bean == null) {
            bean = new ViewTutorsBean();
        }
        request.setAttribute("tutorsBean", bean);
        RenderUtils.invalidateViewState();
        return mapping.findForward("viewTutors");
    }

    public static class ContextDegreesProvider implements DataProvider {

        @Override
        public Converter getConverter() {
            return new DomainObjectKeyConverter();
        }

        @Override
        public Object provide(Object source, Object arg1) {
            final List<ExecutionDegree> executionDegrees = new ArrayList<ExecutionDegree>();
            final ViewTutorsBean bean = (ViewTutorsBean) source;
            final ExecutionSemester executionPeriod = bean.getExecutionSemester();
            if (executionPeriod != null) {
                final ExecutionYear executionYear = executionPeriod.getExecutionYear();
                for (ExecutionDegree executionDegree : executionYear.getExecutionDegreesSet()) {
                    DegreeType degreeType = executionDegree.getDegreeType();
                    if (degreeType == DegreeType.BOLONHA_INTEGRATED_MASTER_DEGREE || degreeType == DegreeType.BOLONHA_DEGREE) {
                        executionDegrees.add(executionDegree);
                    }
                }
            }
            Collections.sort(executionDegrees, ExecutionDegree.EXECUTION_DEGREE_COMPARATORY_BY_DEGREE_TYPE_AND_NAME);
            return executionDegrees;
        }
    }

    public static class ExecutionSemestersProvider implements DataProvider {

        @Override
        public Object provide(Object source, Object currentValue) {
            List<ExecutionSemester> executionSemesters = new ArrayList<ExecutionSemester>();
            for (ExecutionSemester executionSemester : RootDomainObject.getInstance().getExecutionPeriods()) {
                if (executionSemester.isFor(SEMESTER)) {
                    executionSemesters.add(executionSemester);
                }
            }
            Collections.sort(executionSemesters, new ReverseComparator());
            return executionSemesters;
        }

        @Override
        public Converter getConverter() {
            return new DomainObjectKeyConverter();
        }
    }

    public static class ViewTutorsBean implements Serializable {

        private ExecutionSemester executionSemester;
        private ExecutionDegree executionDegree;

        public List<Person> getTutors() {
            List<Person> tutors = new ArrayList<Person>();
            if (getExecutionDegree() != null) {
                for (TutorshipIntention tutorshipIntention : getExecutionDegree().getDegreeCurricularPlan()
                        .getTutorshipIntentionSet()) {
                    if (tutorshipIntention.getAcademicInterval().equals(getExecutionDegree().getAcademicInterval())) {
                        tutors.add(tutorshipIntention.getTeacher().getPerson());
                    }
                }
            }
            return tutors;
        }

        public ExecutionSemester getExecutionSemester() {
            return executionSemester;
        }

        public void setExecutionSemester(ExecutionSemester executionSemester) {
            this.executionSemester = executionSemester;
        }

        public ExecutionDegree getExecutionDegree() {
            return executionDegree;
        }

        public void setExecutionDegree(ExecutionDegree executionDegree) {
            this.executionDegree = executionDegree;
        }
    }
}
