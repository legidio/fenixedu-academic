/*
 * Frequenta.java
 *
 * Created on 20 de Outubro de 2002, 14:42
 */

package Dominio;

/**
 *
 * @author  tfc130
 */
public class Frequenta extends DomainObject implements IFrequenta {
	protected IStudent _aluno;
	protected IDisciplinaExecucao _disciplinaExecucao;
	protected IEnrolment _enrolment;

	// c�digos internos da base de dados
	private Integer _chaveAluno;
	private Integer _chaveDisciplinaExecucao;

	private Integer _keyEnrolment;

	/** Construtor sem argumentos p�blico requerido pela moldura de objectos OJB */
	public Frequenta() {
	}

	public Frequenta(IStudent aluno, IDisciplinaExecucao disciplinaExecucao) {
		setAluno(aluno);
		setDisciplinaExecucao(disciplinaExecucao);
	}

	public Frequenta(IStudent aluno, IDisciplinaExecucao disciplinaExecucao, IEnrolment enrolment) {
		setAluno(aluno);
		setDisciplinaExecucao(disciplinaExecucao);
		setEnrolment(enrolment);
	}

	public IStudent getAluno() {
		return _aluno;
	}

	public void setAluno(IStudent aluno) {
		_aluno = aluno;
	}

	public Integer getChaveAluno() {
		return _chaveAluno;
	}

	public void setChaveAluno(Integer chaveAluno) {
		_chaveAluno = chaveAluno;
	}

	public IDisciplinaExecucao getDisciplinaExecucao() {
		return _disciplinaExecucao;
	}

	public void setDisciplinaExecucao(IDisciplinaExecucao disciplinaExecucao) {
		_disciplinaExecucao = disciplinaExecucao;
	}

	public Integer getChaveDisciplinaExecucao() {
		return _chaveDisciplinaExecucao;
	}

	public void setChaveDisciplinaExecucao(Integer chaveDisciplinaExecucao) {
		_chaveDisciplinaExecucao = chaveDisciplinaExecucao;
	}

	public boolean equals(Object obj) {
		boolean resultado = false;
		if (obj instanceof IFrequenta) {
			IFrequenta frequenta = (IFrequenta) obj;
				resultado = //getCodigoInterno().equals(((Frequenta)obj).getCodigoInterno());
	getAluno().equals(frequenta.getAluno()) && getDisciplinaExecucao().equals(getDisciplinaExecucao());
		}
		return resultado;
	}

	public String toString() {
		String result = "[ATTEND";
		result += ", codigoInterno=" + getIdInternal();
		result += ", Student=" + _aluno;
		result += ", ExecutionCourse=" + _disciplinaExecucao;
		result += ", Enrolment=" + _enrolment;
		result += "]";
		return result;
	}

	/**
	 * @return
	 */
	public Integer getKeyEnrolment() {
		return _keyEnrolment;
	}

	/**
	 * @param integer
	 */
	public void setKeyEnrolment(Integer integer) {
		_keyEnrolment = integer;
	}

	public IEnrolment getEnrolment() {
		return _enrolment;
	}

	public void setEnrolment(IEnrolment enrolment) {
		this._enrolment = enrolment;
		
	}

}
