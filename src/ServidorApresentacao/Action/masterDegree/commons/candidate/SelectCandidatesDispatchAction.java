package ServidorApresentacao.Action.masterDegree.commons.candidate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.util.LabelValueBean;

import framework.factory.ServiceManagerServiceFactory;

import DataBeans.InfoCandidateApproval;
import DataBeans.InfoCandidateApprovalGroup;
import DataBeans.InfoExecutionDegree;
import DataBeans.InfoMasterDegreeCandidate;
import ServidorAplicacao.IUserView;
import ServidorAplicacao.Servico.exceptions.ExistingServiceException;
import ServidorAplicacao.Servico.exceptions.NonExistingServiceException;
import ServidorApresentacao.Action.exceptions.ExistingActionException;
import ServidorApresentacao.Action.exceptions.NonExistingActionException;
import ServidorApresentacao.Action.sop.utils.SessionConstants;
import Util.SituationName;

/**
 * @author Nuno Nunes (nmsn@rnl.ist.utl.pt) Joana Mota (jccm@rnl.ist.utl.pt)
 */
public class SelectCandidatesDispatchAction extends DispatchAction
{

    public ActionForward prepareSelectCandidates(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        HttpSession session = request.getSession(false);

        DynaActionForm approvalForm = (DynaActionForm) form;

        IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);
        String executionYear = (String) request.getAttribute("executionYear");
        String degree = (String) request.getAttribute("degree");
        Integer executionDegree = Integer.valueOf(request.getParameter("executionDegreeID"));

        if (executionYear == null)
        {
            executionYear = (String) approvalForm.get("executionYear");
        }

        if (degree == null)
        {
            degree = (String) approvalForm.get("degree");
        }

        // Get Numerus Clausus
        Integer numerusClausus = null;
        try
        {
            Object args[] = {executionDegree};
            numerusClausus = (Integer) ServiceManagerServiceFactory.executeService(userView,
                    "ReadNumerusClausus", args);
        }
        catch (NonExistingServiceException e)
        {
            throw new NonExistingActionException(e);
        }
        if (numerusClausus == null)
        {
            ActionErrors errors = new ActionErrors();
            errors.add("numerusClaususNotDefined", new ActionError("error.numerusClausus.notDefined"));
            saveErrors(request, errors);
            return mapping.findForward("NumerusClaususNotDefined");
        }
        else
        {
            request.setAttribute("numerusClausus", numerusClausus);
        }

        //Create the Candidate Situation List
        List situationsList = new ArrayList();
        situationsList.add(new LabelValueBean(SituationName.ADMITIDO_STRING,
                SituationName.ADMITIDO_STRING));
        situationsList.add(new LabelValueBean(SituationName.ADMITED_SPECIALIZATION_STRING,
                SituationName.ADMITED_SPECIALIZATION_STRING));
        situationsList.add(new LabelValueBean(SituationName.ADMITED_CONDICIONAL_CURRICULAR_STRING,
                SituationName.ADMITED_CONDICIONAL_CURRICULAR_STRING));
        situationsList.add(new LabelValueBean(SituationName.ADMITED_CONDICIONAL_FINALIST_STRING,
                SituationName.ADMITED_CONDICIONAL_FINALIST_STRING));
        situationsList.add(new LabelValueBean(SituationName.ADMITED_CONDICIONAL_OTHER_STRING,
                SituationName.ADMITED_CONDICIONAL_OTHER_STRING));
        situationsList.add(new LabelValueBean(SituationName.NAO_ACEITE_STRING,
                SituationName.NAO_ACEITE_STRING));
        situationsList.add(new LabelValueBean(SituationName.SUPLENTE_STRING,
                SituationName.SUPLENTE_STRING));
        situationsList.add(new LabelValueBean(SituationName.SUBSTITUTE_CONDICIONAL_CURRICULAR_STRING,
                SituationName.SUBSTITUTE_CONDICIONAL_CURRICULAR_STRING));
        situationsList.add(new LabelValueBean(SituationName.SUBSTITUTE_CONDICIONAL_FINALIST_STRING,
                SituationName.SUBSTITUTE_CONDICIONAL_FINALIST_STRING));
        situationsList.add(new LabelValueBean(SituationName.SUBSTITUTE_CONDICIONAL_OTHER_STRING,
                SituationName.SUBSTITUTE_CONDICIONAL_OTHER_STRING));

        request.setAttribute(SessionConstants.CANDIDATE_SITUATION_LIST, situationsList);

        List candidateList = null;

        List activeSituations = new ArrayList();
        activeSituations.add(new SituationName(SituationName.PENDENT_CONFIRMADO));

        Object args[] = {executionDegree, activeSituations};

        try
        {
            candidateList = (ArrayList) ServiceManagerServiceFactory.executeService(userView,
                    "ReadCandidatesForSelection", args);

        }
        catch (NonExistingServiceException e)
        {
            ActionErrors errors = new ActionErrors();
            errors.add("nonExisting", new ActionError(
                    "error.masterDegree.administrativeOffice.nonExistingCandidates"));
            saveErrors(request, errors);
            return mapping.getInputForward();

        }
        catch (ExistingServiceException e)
        {
            throw new ExistingActionException(e);
        }

        BeanComparator nameComparator = new BeanComparator("infoPerson.nome");
        Collections.sort(candidateList, nameComparator);

        String ids[] = new String[candidateList.size()];
        String situations[] = new String[candidateList.size()];
        String remarks[] = new String[candidateList.size()];
        String substitutes[] = new String[candidateList.size()];

        Iterator iterator = candidateList.iterator();
        int i = 0;
        while (iterator.hasNext())
        {
            ids[i++] = ((InfoMasterDegreeCandidate) iterator.next()).getIdInternal().toString();
        }

        approvalForm.set("candidatesID", ids);
        approvalForm.set("situations", situations);
        approvalForm.set("remarks", remarks);
        approvalForm.set("substitutes", substitutes);
        approvalForm.set("executionYear", executionYear);
        approvalForm.set("degree", degree);

        generateToken(request);
        saveToken(request);
        request.setAttribute(SessionConstants.EXECUTION_DEGREE, executionDegree);
        request.setAttribute("candidateList", candidateList);
        return mapping.findForward("PrepareSuccess");
    }

    public ActionForward selectCandidates(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        HttpSession session = request.getSession(false);

        DynaActionForm approvalForm = (DynaActionForm) form;

        IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

        if (!isTokenValid(request)) { return mapping.findForward("BackError"); }

        String[] candidateList = (String[]) approvalForm.get("situations");
        String[] ids = (String[]) approvalForm.get("candidatesID");
        String[] remarks = (String[]) approvalForm.get("remarks");
        String executionYear = (String) approvalForm.get("executionYear");
        String degree = (String) approvalForm.get("degree");
        Integer executionDegree = null;
        executionDegree = Integer.valueOf(request.getParameter("executionDegreeID"));
        /*
         * if (request.getParameter("executionDegreeID") == null){
         * executionDegree =
         * Integer.valueOf((String)request.getAttribute("executionDegreeID")); }
         * else{ executionDegree =
         * Integer.valueOf(request.getParameter("executionDegreeID")); }
         */
        List candidatesAdmited = new ArrayList();

        try
        {
            Object args[] = {candidateList, ids};
            candidatesAdmited = (ArrayList) ServiceManagerServiceFactory.executeService(userView,
                    "ReadAdmitedCandidates", args);
        }
        catch (ExistingServiceException e)
        {
            throw new ExistingActionException(e);
        }

        request.setAttribute("candidatesID", ids);
        request.setAttribute("situations", candidateList);
        request.setAttribute("remarks", remarks);
        request.setAttribute("executionYear", executionYear);
        request.setAttribute("degree", degree);
        request.setAttribute(SessionConstants.EXECUTION_DEGREE, String.valueOf(executionDegree));

        // Get Numerus Clausus
        Integer numerusClausus = null;
        try
        {
            //Object args[] = { executionYear, degree };
            Object args[] = {executionDegree};
            numerusClausus = (Integer) ServiceManagerServiceFactory.executeService(userView,
                    "ReadNumerusClausus", args);
        }
        catch (NonExistingServiceException e)
        {
            throw new NonExistingActionException(e);
        }
        if (candidatesAdmited.size() > numerusClausus.intValue())
        {
            generateToken(request);
            saveToken(request);
            return mapping.findForward("RequestConfirmation");
        }

        if (hasSubstitutes(candidateList)) { return mapping.findForward("OrderCandidates"); }

        return mapping.findForward("ChooseSuccess");
    }

    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {

        HttpSession session = request.getSession(false);
        IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

        DynaActionForm approvalForm = (DynaActionForm) form;

        if (!isTokenValid(request)) { return mapping.findForward("BackError"); }

        String[] candidateList = (String[]) approvalForm.get("situations");
        String[] ids = (String[]) approvalForm.get("candidatesID");
        String[] remarks = (String[]) approvalForm.get("remarks");

        approvalForm.set("executionYear", approvalForm.get("executionYear"));
        approvalForm.set("degree", approvalForm.get("degree"));

        String substitutes[] = new String[candidateList.length];
        approvalForm.set("substitutes", substitutes);
        request.setAttribute("candidatesID", ids);
        request.setAttribute("situations", candidateList);
        request.setAttribute("remarks", remarks);

        if ((request.getParameter("OK") != null) && (request.getParameter("notOK") == null))
        {
            Object args[] = {candidateList, ids};
            List result = null;
            try
            {
                result = (ArrayList) ServiceManagerServiceFactory.executeService(userView,
                        "ReadSubstituteCandidates", args);
            }
            catch (ExistingServiceException e)
            {
                throw new ExistingActionException(e);
            }

            if (hasSubstitutes(candidateList))
            {
                request.setAttribute("candidateList", result);
                return mapping.findForward("OrderCandidates");
            }
            else
            {
                return mapping.findForward("ChooseSuccess");
            }
        }
        else
        {
            return mapping.findForward("Cancel");
        }
    }

    public ActionForward getSubstituteCandidates(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        HttpSession session = request.getSession(false);

        DynaActionForm substituteForm = (DynaActionForm) form;

        IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

        if (!isTokenValid(request))
        {
            return mapping.findForward("BackError");
        }
        else
        {
            generateToken(request);
            saveToken(request);
        }

        String[] candidateList = (String[]) substituteForm.get("situations");
        String[] ids = (String[]) substituteForm.get("candidatesID");
        String[] remarks = (String[]) substituteForm.get("remarks");
        String[] substitutes = (String[]) substituteForm.get("substitutes");

        request.setAttribute("substitutes", substitutes);
        request.setAttribute("candidatesID", ids);
        request.setAttribute("situations", candidateList);
        request.setAttribute("remarks", remarks);

        List result = null;
        Object args[] = {ids};
        try
        {
            result = (List) ServiceManagerServiceFactory
                    .executeService(userView, "ReadCandidates", args);
        }
        catch (ExistingServiceException e)
        {
            throw new ExistingActionException(e);
        }
        request.setAttribute("candidateList", result);

        return mapping.findForward("OrderCandidatesReady");
    }

    public ActionForward preparePresentation(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        HttpSession session = request.getSession(false);

        DynaActionForm resultForm = (DynaActionForm) form;

        IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

        String[] candidateList = (String[]) resultForm.get("situations");
        String[] ids = (String[]) resultForm.get("candidatesID");
        String[] remarks = (String[]) resultForm.get("remarks");
        String[] substitutes = (String[]) resultForm.get("substitutes");
        String executionDegree = (String) request.getAttribute(SessionConstants.EXECUTION_DEGREE);
        if (!isTokenValid(request)) { return mapping.findForward("BackError"); }

        request.setAttribute("situations", candidateList);
        request.setAttribute("candidatesID", ids);
        request.setAttribute("remarks", remarks);
        request.setAttribute("substitutes", substitutes);
        request.setAttribute(SessionConstants.EXECUTION_DEGREE, executionDegree);

        resultForm.set("executionYear", resultForm.get("executionYear"));
        resultForm.set("degree", resultForm.get("degree"));

        try
        {
            if (!validSubstituteForm(candidateList, substitutes))
            {
                ActionErrors errors = new ActionErrors();
                errors.add("nonExisting", new ActionError("error.invalidOrdering"));
                saveErrors(request, errors);
                return mapping.findForward("OrderCandidates");
            }
        }
        catch (NumberFormatException e)
        {
            ActionErrors errors = new ActionErrors();
            errors.add("nonExisting", new ActionError("error.numberError"));
            saveErrors(request, errors);
            return mapping.findForward("OrderCandidates");
        }

        List candidates = new ArrayList();

        Object args[] = {ids};
        try
        {
            candidates = (List) ServiceManagerServiceFactory.executeService(userView, "ReadCandidates",
                    args);
        }
        catch (ExistingServiceException e)
        {
            throw new ExistingActionException(e);
        }

        List result = getLists(candidateList, ids, remarks, substitutes, candidates);

        sortLists(result);

        request.setAttribute("infoGroup", result);

        if (request.getAttribute("confirmation") != null)
        {
            request.setAttribute("confirmation", request.getAttribute("confirmation"));
        }
        else
        {
            request.setAttribute("confirmation", "YES");
            generateToken(request);
            saveToken(request);
        }

        //		
        //		Iterator iterator = result.iterator();
        //		while(iterator.hasNext()){
        //			InfoCandidateApprovalGroup infoCandidateApprovalGroup =
        // (InfoCandidateApprovalGroup) iterator.next();
        //			Iterator iter =
        // infoCandidateApprovalGroup.getCandidates().iterator();
        //			System.out.println(infoCandidateApprovalGroup.getSituationName());
        //			while(iter.hasNext()){
        //				InfoCandidateApproval infoCandidateApproval = (InfoCandidateApproval)
        // iter.next();
        //				System.out.println("-----------");
        //				System.out.println(" " + infoCandidateApproval.getIdInternal());
        //				System.out.println(" " + infoCandidateApproval.getCandidateName());
        //				System.out.println(" " + infoCandidateApproval.getRemarks());
        //				System.out.println(" " + infoCandidateApproval.getSituationName());
        //				System.out.println(" " + infoCandidateApproval.getOrderPosition());
        //			}
        //		}
        //
        //
        //		
        return mapping.findForward("FinalPresentation");
    }

    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {

        HttpSession session = request.getSession(false);

        DynaActionForm resultForm = (DynaActionForm) form;

        IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

        String[] candidateList = (String[]) resultForm.get("situations");
        String[] ids = (String[]) resultForm.get("candidatesID");
        String[] remarks = (String[]) resultForm.get("remarks");
        String[] substitutes = (String[]) resultForm.get("substitutes");
        Integer executionDegree = Integer.valueOf(request.getParameter("executionDegreeID"));

        request.setAttribute("situations", candidateList);
        request.setAttribute("candidatesID", ids);
        request.setAttribute("remarks", remarks);
        request.setAttribute("substitutes", substitutes);

        List candidates = new ArrayList();

        try
        {
            Object args[] = {ids};
            candidates = (List) ServiceManagerServiceFactory.executeService(userView, "ReadCandidates",
                    args);
        }
        catch (ExistingServiceException e)
        {
            throw new ExistingActionException(e);
        }

        List result = getLists(candidateList, ids, remarks, substitutes, candidates);

        sortLists(result);

        //		Iterator iterator = result.iterator();
        //		while(iterator.hasNext()){
        //			InfoCandidateApprovalGroup infoCandidateApprovalGroup =
        // (InfoCandidateApprovalGroup) iterator.next();
        //			Iterator iter =
        // infoCandidateApprovalGroup.getCandidates().iterator();
        //			System.out.println(infoCandidateApprovalGroup.getSituationName());
        //			while(iter.hasNext()){
        //				InfoCandidateApproval infoCandidateApproval = (InfoCandidateApproval)
        // iter.next();
        //				System.out.println("-----------");
        //				System.out.println(" " + infoCandidateApproval.getIdInternal());
        //				System.out.println(" " + infoCandidateApproval.getCandidateName());
        //				System.out.println(" " + infoCandidateApproval.getRemarks());
        //				System.out.println(" " + infoCandidateApproval.getSituationName());
        //				System.out.println(" " + infoCandidateApproval.getOrderPosition());
        //			}
        //		}

        request.setAttribute("infoGroup", result);

        InfoExecutionDegree infoExecutionDegree = null;

        try
        {
            // Object args[] = { resultForm.get("executionYear"),
            // resultForm.get("degree")};
            Object args[] = {executionDegree};
            infoExecutionDegree = (InfoExecutionDegree) ServiceManagerServiceFactory.executeService(
                    userView, "ReadExecutionDegreeByOID", args);
        }
        catch (ExistingServiceException e)
        {
            throw new ExistingActionException(e);
        }

        request.setAttribute("infoExecutionDegree", infoExecutionDegree);

        if (request.getAttribute("confirmation") != null)
        {
            request.setAttribute("confirmation", request.getAttribute("confirmation"));
        }
        else
        {
            request.setAttribute("confirmation", "PRINT_PAGE");
        }
        return mapping.findForward("PrintReady");
    }

    /**
     * @param candidateList
     * @param substitutes
     * @return
     */
    private boolean validSubstituteForm(String[] situations, String[] substitutes)
            throws NumberFormatException
    {
        List aux = new ArrayList();

        for (int i = 0; i < situations.length; i++)
        {
            if ((situations[i].equals(SituationName.SUPLENTE_STRING))
                    || (situations[i].equals(SituationName.SUBSTITUTE_CONDICIONAL_CURRICULAR_STRING))
                    || (situations[i].equals(SituationName.SUBSTITUTE_CONDICIONAL_FINALIST_STRING))
                    || (situations[i].equals(SituationName.SUBSTITUTE_CONDICIONAL_OTHER_STRING)))
            {
                aux.add(new Integer(substitutes[i]));
            }
        }

        if (aux.size() == 0) { return true; }

        Collections.sort(aux);

        Iterator iterator = aux.iterator();
        Integer previous = null;

        previous = (Integer) iterator.next();

        if (previous.intValue() != 1) { return false; }

        while (iterator.hasNext())
        {
            Integer actual = (Integer) iterator.next();

            if (actual.intValue() != (previous.intValue() + 1)) { return false; }
            previous = actual;
        }
        return true;
    }

    public ActionForward aprove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {

        HttpSession session = request.getSession(false);

        DynaActionForm substituteForm = (DynaActionForm) form;

        IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

        //		if (!isTokenValid(request)){
        //			return mapping.findForward("BackError");
        //		} else {
        //			generateToken(request);
        //			saveToken(request);
        //		}

        String[] candidateList = (String[]) substituteForm.get("situations");
        String[] ids = (String[]) substituteForm.get("candidatesID");
        String[] remarks = (String[]) substituteForm.get("remarks");
        String[] substitutes = (String[]) substituteForm.get("substitutes");
        Integer executionDegree = Integer.valueOf(request.getParameter("executionDegreeID"));

        substituteForm.set("executionYear", substituteForm.get("executionYear"));
        substituteForm.set("degree", substituteForm.get("degree"));

        if ((request.getParameter("OK") != null) && (request.getParameter("notOK") == null))
        {

            Object args[] = {candidateList, ids, remarks, substitutes};
            try
            {
                ServiceManagerServiceFactory.executeService(userView, "ApproveCandidates", args);
            }
            catch (ExistingServiceException e)
            {
                throw new ExistingActionException(e);
            }

            substituteForm.set("substitutes", substitutes);
            request.setAttribute("candidatesID", ids);
            request.setAttribute("situations", candidateList);
            request.setAttribute("remarks", remarks);
            request.setAttribute("confirmation", "NO");
            request.setAttribute(SessionConstants.EXECUTION_DEGREE, String.valueOf(executionDegree));

            return mapping.findForward("ChooseSuccess");
        }
        return mapping.findForward("Cancel");
    }

    /**
     * @param result
     */
    private void sortLists(List result)
    {

        Iterator iterator = result.iterator();
        while (iterator.hasNext())
        {
            InfoCandidateApprovalGroup infoCandidateApprovalGroup = (InfoCandidateApprovalGroup) iterator
                    .next();
            BeanComparator comparator = null;

            if ((infoCandidateApprovalGroup.getSituationName().equals(SituationName.ADMITIDO_STRING))
                    || (infoCandidateApprovalGroup.getSituationName()
                            .equals(SituationName.ADMITED_SPECIALIZATION_STRING)))
            {
                comparator = new BeanComparator("candidateName");
            }
            else if (infoCandidateApprovalGroup.getSituationName().equals(SituationName.SUPLENTE_STRING))
            {
                comparator = new BeanComparator("orderPosition");
            }
            else if (infoCandidateApprovalGroup.getSituationName().equals(
                    SituationName.NAO_ACEITE_STRING))
            {
                comparator = new BeanComparator("candidateName");
            }
            Collections.sort(infoCandidateApprovalGroup.getCandidates(), comparator);
        }
    }

    /**
     * @param candidateList
     * @param ids
     * @param remarks
     * @param substitutes
     * @return
     */
    private List getLists(String[] candidateList, String[] ids, String[] remarks, String[] substitutes,
            List candidates)
    {
        InfoCandidateApprovalGroup approvedList = new InfoCandidateApprovalGroup();
        approvedList.setSituationName(SituationName.ADMITIDO_STRING);

        InfoCandidateApprovalGroup notApprovedList = new InfoCandidateApprovalGroup();
        notApprovedList.setSituationName(SituationName.NAO_ACEITE_STRING);

        InfoCandidateApprovalGroup substitutesList = new InfoCandidateApprovalGroup();
        substitutesList.setSituationName(SituationName.SUPLENTE_STRING);

        InfoCandidateApprovalGroup specializationList = new InfoCandidateApprovalGroup();
        specializationList.setSituationName(SituationName.ADMITED_SPECIALIZATION_STRING);

        for (int i = 0; i < candidateList.length; i++)
        {
            InfoCandidateApproval infoCandidateApproval = new InfoCandidateApproval();
            infoCandidateApproval.setCandidateName(((InfoMasterDegreeCandidate) candidates.get(i))
                    .getInfoPerson().getNome());
            infoCandidateApproval.setIdInternal(new Integer(ids[i]));
            if ((substitutes[i] != null) && (substitutes[i].length() > 0))
            {
                infoCandidateApproval.setOrderPosition(new Integer(substitutes[i]));
            }
            else
            {
                infoCandidateApproval.setOrderPosition(null);
            }

            infoCandidateApproval.setRemarks(remarks[i]);
            infoCandidateApproval.setSituationName(candidateList[i]);

            if ((candidateList[i].equals(SituationName.ADMITED_SPECIALIZATION_STRING)))
            {
                specializationList.getCandidates().add(infoCandidateApproval);
            }
            else if ((candidateList[i].equals(SituationName.ADMITIDO_STRING))
                    || (candidateList[i].equals(SituationName.ADMITED_CONDICIONAL_CURRICULAR_STRING))
                    || (candidateList[i].equals(SituationName.ADMITED_CONDICIONAL_FINALIST_STRING))
                    || (candidateList[i].equals(SituationName.ADMITED_CONDICIONAL_OTHER_STRING)))
            {
                approvedList.getCandidates().add(infoCandidateApproval);
            }
            else if ((candidateList[i].equals(SituationName.SUPLENTE_STRING))
                    || (candidateList[i].equals(SituationName.SUBSTITUTE_CONDICIONAL_CURRICULAR_STRING))
                    || (candidateList[i].equals(SituationName.SUBSTITUTE_CONDICIONAL_FINALIST_STRING))
                    || (candidateList[i].equals(SituationName.SUBSTITUTE_CONDICIONAL_OTHER_STRING)))
            {
                substitutesList.getCandidates().add(infoCandidateApproval);
            }
            else if (candidateList[i].equals(SituationName.NAO_ACEITE_STRING))
            {
                notApprovedList.getCandidates().add(infoCandidateApproval);
            }
        }

        List result = new ArrayList();
        result.add(approvedList);
        result.add(substitutesList);
        result.add(notApprovedList);
        result.add(specializationList);

        return result;
    }

    private static boolean hasSubstitutes(String[] list)
    {
        int size = list.length;
        int i = 0;
        for (i = 0; i < size; i++)
        {
            if (list[i].equals(SituationName.SUPLENTE_STRING)
                    || list[i].equals(SituationName.SUBSTITUTE_CONDICIONAL_CURRICULAR_STRING)
                    || list[i].equals(SituationName.SUBSTITUTE_CONDICIONAL_FINALIST_STRING)
                    || list[i].equals(SituationName.SUBSTITUTE_CONDICIONAL_OTHER_STRING))
                return true;
        }
        return false;
    }

}