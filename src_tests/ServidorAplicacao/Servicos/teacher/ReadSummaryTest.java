package ServidorAplicacao.Servicos.teacher;

import DataBeans.InfoSiteSummary;
import DataBeans.InfoSummary;
import DataBeans.SiteView;
import DataBeans.util.Cloner;
import Dominio.ISummary;
import Dominio.Summary;
import ServidorAplicacao.IUserView;
import ServidorAplicacao.Servico.Autenticacao;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorPersistente.IPersistentSummary;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;

/**
 * @author Leonor Almeida
 * @author S�rgio Montelobo
 */
public class ReadSummaryTest extends SummaryBelongsExecutionCourseTestCase {

	/**
	 * @param testName
	 */
	public ReadSummaryTest(String testName) {
		super(testName);
	}

	protected String getDataSetFilePath() {
		return "etc/testReadSummaryDataSet.xml";
	}

	protected String getNameOfServiceToBeTested() {
		return "ReadSummary";
	}

	protected String[] getAuthorizedUser() {

		String[] args = { "user", "pass", getApplication()};
		return args;
	}

	protected String[] getUnauthorizedUser() {

		String[] args = { "julia", "pass", getApplication()};
		return args;
	}

	protected String[] getNonTeacherUser() {

		String[] args = { "jccm", "pass", getApplication()};
		return args;
	}

	protected Object[] getAuthorizeArguments() {

		Integer executionCourseId = new Integer(24);
		Integer summaryId = new Integer(261);

		Object[] args = { executionCourseId, summaryId };
		return args;
	}

	protected Object[] getTestSummarySuccessfullArguments() {

		Integer executionCourseId = new Integer(24);
		Integer summaryId = new Integer(261);

		Object[] args = { executionCourseId, summaryId };
		return args;
	}

	protected Object[] getTestSummaryUnsuccessfullArguments() {

		Integer executionCourseId = new Integer(25);
		Integer summaryId = new Integer(261);

		Object[] args = { executionCourseId, summaryId };
		return args;
	}

	protected String getApplication() {
		return Autenticacao.EXTRANET;
	}

	public void testSuccessfull() {

		try {
			SiteView result = null;

			String[] args = getAuthorizedUser();
			IUserView userView = authenticateUser(args);

			result =
				(SiteView) gestor.executar(
					userView,
					getNameOfServiceToBeTested(),
					getAuthorizeArguments());

			ISummary newSummary = new Summary(new Integer(261));
			ISuportePersistente sp = SuportePersistenteOJB.getInstance();
			sp.iniciarTransaccao();
			IPersistentSummary persistentSummary = sp.getIPersistentSummary();
			newSummary =
				(ISummary) persistentSummary.readByOId(newSummary, false);
			sp.confirmarTransaccao();

			InfoSiteSummary infoSiteSummary =
				(InfoSiteSummary) result.getComponent();
			InfoSummary infoSummary = infoSiteSummary.getInfoSummary();
			ISummary oldSummary = Cloner.copyInfoSummary2ISummary(infoSummary);

			assertEquals(newSummary, oldSummary);
			// verifica se a base de dados nao foi alterada
			compareDataSet(getDataSetFilePath());
		}
		catch (FenixServiceException ex) {
			fail("Reading the Summary of a Site" + ex);
		}
		catch (Exception ex) {
			fail("Reading the Summary of a Site" + ex);
		}
	}
}