package de.randi2.jsf.controllerBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import lombok.Getter;
import lombok.Setter;
import de.randi2.jsf.backingBeans.Randi2Page;
import de.randi2.jsf.backingBeans.SimulationAlgorithm;
import de.randi2.jsf.backingBeans.SimulationSubjectProperty;
import de.randi2.jsf.backingBeans.Step5;
import de.randi2.jsf.wrappers.AlgorithmWrapper;
import de.randi2.jsf.wrappers.CriterionWrapper;
import de.randi2.jsf.wrappers.DistributedConstraintWrapper;
import de.randi2.jsf.wrappers.DistributedCriterionWrapper;
import de.randi2.jsf.wrappers.DistributionTrialSiteWrapper;
import de.randi2.jsf.wrappers.TrialSiteRatioWrapper;
import de.randi2.model.TreatmentArm;
import de.randi2.model.Trial;
import de.randi2.model.TrialSite;
import de.randi2.model.criteria.AbstractCriterion;
import de.randi2.model.criteria.constraints.AbstractConstraint;
import de.randi2.model.randomization.BiasedCoinRandomizationConfig;
import de.randi2.model.randomization.BlockRandomizationConfig;
import de.randi2.model.randomization.CompleteRandomizationConfig;
import de.randi2.model.randomization.MinimizationConfig;
import de.randi2.model.randomization.TruncatedBinomialDesignConfig;
import de.randi2.model.randomization.UrnDesignConfig;
import de.randi2.simulation.model.DistributionSubjectProperty;
import de.randi2.simulation.model.SimualtionResultArm;
import de.randi2.simulation.model.SimulationResult;
import de.randi2.simulation.service.SimulationService;

/**
 * <p>
 * This class cares about the simulation of trials and contains all the needed
 * methods to work with this object for the UI.
 * </p>
 * 
 * @author ds@randi2.de
 */
public class SimulationHandler extends AbstractTrialHandler {

	@Getter
	@Setter
	private TrialHandler trialHandler;

	@Getter
	@Setter
	private SimulationService simulationService;

	@Getter
	@Setter
	private int runs = 1000;

	@Getter
	@Setter
	private long maxTime;

	@Getter
	private SimulationResult simResult;

	@Getter
	private int countTrialSites;

	@Setter
	private boolean criterionChanged = true;

	public void criterionChanged() {
		criterionChanged = true;
	}

	public boolean isCriterionChanged() {
		System.out.println("criterion changed");
		criterionChanged = true;
		return criterionChanged;
	}

	@Getter
	private List<AlgorithmWrapper> randomisationConfigs = new ArrayList<AlgorithmWrapper>();

	@Getter
	@Setter
	private List<SimulationResult> simulationResults = new ArrayList<SimulationResult>();

	@Getter
	@Setter
	private boolean simOnly;

	@Getter
	@Setter
	private boolean seedRandomisationAlgorithmB;

	@Getter
	@Setter
	private long seedRandomisationAlgorithm;

	@Getter
	@Setter
	private boolean simFromTrialCreationFirst = true;

	private DistributionTrialSiteWrapper distributedTrialSites;

	private List<DistributedCriterionWrapper<Serializable, AbstractConstraint<Serializable>>> distributedCriterions;

	public DistributionTrialSiteWrapper getDistributionTrialSiteWrapper() {
		if (distributedTrialSites == null
				|| distributedTrialSites.getTrialSitesRatioWrappers().size() != countTrialSites) {
			List<TrialSiteRatioWrapper> tRatioWrapper = new ArrayList<TrialSiteRatioWrapper>();
			for (TrialSite site : showedObject.getParticipatingSites()) {
				tRatioWrapper.add(new TrialSiteRatioWrapper(site));
			}
			distributedTrialSites = new DistributionTrialSiteWrapper(
					tRatioWrapper);
		}
		return distributedTrialSites;
	}

	public void setDistributedCriterions(
			List<DistributedCriterionWrapper<Serializable, AbstractConstraint<Serializable>>> distributedCriterions) {
		this.distributedCriterions = distributedCriterions;
	}

	@SuppressWarnings("unchecked")
	public List<DistributedCriterionWrapper<Serializable, AbstractConstraint<Serializable>>> getDistributedCriterions() {
		if (distributedCriterions == null
				|| distributedCriterions.size() != showedObject.getCriteria()
						.size()) {
			distributedCriterions = new ArrayList<DistributedCriterionWrapper<Serializable, AbstractConstraint<Serializable>>>();
			for (AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> c : showedObject
					.getCriteria()) {
				if (c.getStrata() != null) {
					List<DistributedConstraintWrapper> strataDistributions = new ArrayList<DistributedConstraintWrapper>();

					for (AbstractConstraint<? extends Serializable> con : c
							.getStrata()) {
						strataDistributions
								.add(new DistributedConstraintWrapper(con));
					}
					distributedCriterions.add(new DistributedCriterionWrapper(
							strataDistributions,
							new CriterionWrapper<Serializable>(
									(AbstractCriterion<Serializable, ?>) c)));
				}
			}
		}
		return distributedCriterions;
	}

	public Trial getSimTrial() {
		if (simFromTrialCreationFirst && !simOnly) {
			showedObject = trialHandler.getShowedObject();
			setRandomizationConfig(trialHandler.getRandomizationConfig());
			try {
				/* Leading Trial Site & Sponsor Investigator */
				showedObject.setLeadingSite(trialHandler.getTrialSitesAC()
						.getSelectedObject());
				if (trialHandler.getSponsorInvestigatorsAC()
						.getSelectedObject() != null)
					showedObject.setSponsorInvestigator(trialHandler
							.getSponsorInvestigatorsAC().getSelectedObject()
							.getPerson());

				showedObject.setCriteria(configureCriteriaStep4());
				/* Algorithm Configuration */
				configureAlgorithmWithStep5();
				simFromTrialCreationFirst = false;
			} catch (Exception e) {
				return null;
			}

		} else if (showedObject == null && simOnly) {
			showedObject = new Trial();
			List<TreatmentArm> arms = new ArrayList<TreatmentArm>();
			arms.add(new TreatmentArm());
			arms.add(new TreatmentArm());
			showedObject.setTreatmentArms(arms);
		}
		if (simOnly && criterionChanged) {
			/* SubjectProperties Configuration - done in Step4 */
			ValueExpression ve1 = FacesContext.getCurrentInstance()
					.getApplication().getExpressionFactory()
					.createValueExpression(
							FacesContext.getCurrentInstance().getELContext(),
							"#{simulationSubjectProperty}",
							SimulationSubjectProperty.class);
			SimulationSubjectProperty currentSimulationSubjectProperty = (SimulationSubjectProperty) ve1
					.getValue(FacesContext.getCurrentInstance().getELContext());
			showedObject
					.setCriteria(addAllConfiguredCriteria(currentSimulationSubjectProperty
							.getCriteria()));
			criterionChanged = false;
		}

		return showedObject;

	}

	private List<CriterionWrapper<Serializable>> strata;

	@SuppressWarnings("unchecked")
	public List<CriterionWrapper<Serializable>> getStrata() {
		strata = new ArrayList<CriterionWrapper<Serializable>>();
		for (AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> c : showedObject
				.getCriteria()) {
			strata.add(new CriterionWrapper<Serializable>(
					(AbstractCriterion<Serializable, ?>) c));
		}
		return strata;
	}

	/**
	 * Checks if any strata factors are defined.
	 * 
	 * @return
	 */
	public boolean isStrataFactorsDefined() {

		for (AbstractCriterion<?, ?> c : showedObject.getCriteria()) {
			if (c.getStrata() != null) {
				if (c.getStrata().size() > 0)
					return true;
			}
		}
		return false;
	}

	public String getAlgName() {
		ResourceBundle bundle = ResourceBundle.getBundle(
				"de.randi2.jsf.i18n.algorithms", getLoginHandler()
						.getChosenLocale());
		return bundle.getString(showedObject.getRandomizationConfiguration()
				.getClass().getCanonicalName()
				+ ".name");
	}

	public String getFurtherDetails() {
		StringBuffer furtherDetails = new StringBuffer();
		ResourceBundle bundle = ResourceBundle.getBundle(
				"de.randi2.jsf.i18n.labels", getLoginHandler()
						.getChosenLocale());
		if (BlockRandomizationConfig.class.isInstance(showedObject
				.getRandomizationConfiguration())) {
			BlockRandomizationConfig conf = BlockRandomizationConfig.class
					.cast(showedObject.getRandomizationConfiguration());
			furtherDetails.append("<b>");
			furtherDetails.append(bundle
					.getString("pages.blockR.variableBSize"));
			furtherDetails.append("</b> ");
			furtherDetails.append(conf.isVariableBlockSize());
			furtherDetails.append("<br//>");
			if (conf.isVariableBlockSize()) {
				furtherDetails.append("<b>");
				furtherDetails.append(bundle
						.getString("pages.blockR.minBlockSize"));
				furtherDetails.append("</b> ");
				furtherDetails.append(conf.getMinimum());
				furtherDetails.append("<br//>");
				furtherDetails.append("<b>");
				furtherDetails.append(bundle
						.getString("pages.blockR.maxBlockSize"));
				furtherDetails.append("</b> ");
				furtherDetails.append(conf.getMaximum());
				furtherDetails.append("<br//>");
			} else {
				furtherDetails.append("<b>");
				furtherDetails.append(bundle
						.getString("pages.blockR.blockSize"));
				furtherDetails.append("</b> ");
				furtherDetails.append(conf.getMinimum());
				furtherDetails.append("<br//>");
			}
		} else if (UrnDesignConfig.class.isInstance(showedObject
				.getRandomizationConfiguration())) {
			UrnDesignConfig conf = UrnDesignConfig.class.cast(showedObject
					.getRandomizationConfiguration());
			furtherDetails.append("<b>");
			furtherDetails.append(bundle.getString("pages.urnR.initialCount"));
			furtherDetails.append("</b> ");
			furtherDetails.append(conf.getInitializeCountBalls());
			furtherDetails.append("<br//>");
			furtherDetails.append("<b>");
			furtherDetails.append(bundle.getString("pages.urnR.replacedBalls"));
			furtherDetails.append("</b> ");
			furtherDetails.append(conf.getCountReplacedBalls());
			furtherDetails.append("<br//>");
		} else if (MinimizationConfig.class.isInstance(showedObject
				.getRandomizationConfiguration())) {
			MinimizationConfig conf = MinimizationConfig.class
					.cast(showedObject.getRandomizationConfiguration());
			furtherDetails.append("<b>");
			furtherDetails
					.append(bundle.getString("pages.minimization.pvalue"));
			furtherDetails.append("</b> ");
			furtherDetails.append(conf.getP());
			furtherDetails.append("<br//>");
		} else {
			furtherDetails.append("--");
		}
		return furtherDetails.toString();
	}

	/**
	 * Specifies if the algorithm is stratified or not.
	 * 
	 * @return
	 */
	public boolean isStratified() {
		boolean t = isStrataFactorsDefined();
		if (t)
			return t;
		else
			return showedObject.isStratifyTrialSite();
	}

	public void simTrial() {
		simulationResults = null;
		simResult = null;
		List<DistributionSubjectProperty> properties = new ArrayList<DistributionSubjectProperty>();
		if (distributedCriterions != null) {
			for (DistributedCriterionWrapper<Serializable, AbstractConstraint<Serializable>> dcw : distributedCriterions) {
				properties.add(dcw.getDistributionSubjectProperty());
			}
		}

		if (simOnly) {
			simulationResults = new ArrayList<SimulationResult>();
			for (AlgorithmWrapper alg : randomisationConfigs) {
				showedObject.setRandomizationConfiguration(alg.getConf());
				alg.getConf().setTempData(null);
				alg.getConf().setTrial(showedObject);
				if (seedRandomisationAlgorithmB) {
					alg.getConf().resetAlgorithm(seedRandomisationAlgorithm);
				} else {
					alg.getConf().resetAlgorithm();
				}
				SimulationResult result = simulationService.simulateTrial(
						showedObject, properties, distributedTrialSites
								.getDistributionTrialSites(), runs, maxTime);
				result.setAlgorithmDescription(alg.getDescription());
				simulationResults.add(result);
			}

		} else {
			SimulationResult result = simulationService.simulateTrial(
					showedObject, properties, distributedTrialSites
							.getDistributionTrialSites(), runs, maxTime);
			simResult = result;
			Randi2Page rPage = ((Randi2Page) FacesContext.getCurrentInstance()
					.getApplication().getELResolver().getValue(
							FacesContext.getCurrentInstance()
									.getELContext(), null, "randi2Page"));
			rPage.simulationResult(null);
		}
	}

	public boolean isResultComplete() {
		return simResult != null;
	}

	public boolean isResultsComplete() {
		return (simulationResults != null && !simulationResults.isEmpty());
	}

	public boolean isSimulationComplete() {
		return isResultComplete() || isResultsComplete();
	}

	public void addAlgorithm(ActionEvent event) {
		ValueExpression ve2 = FacesContext.getCurrentInstance()
				.getApplication().getExpressionFactory().createValueExpression(
						FacesContext.getCurrentInstance().getELContext(),
						"#{simulationAlgorithm}", SimulationAlgorithm.class);
		SimulationAlgorithm currentAlg = (SimulationAlgorithm) ve2
				.getValue(FacesContext.getCurrentInstance().getELContext());
		if (currentAlg.getSelectedAlgorithmPanelId().equals(
				Step5.AlgorithmPanelId.COMPLETE_RANDOMIZATION.toString())) {
			randomisationConfigs.add(new AlgorithmWrapper(
					new CompleteRandomizationConfig()));
		} else if (currentAlg.getSelectedAlgorithmPanelId().equals(
				Step5.AlgorithmPanelId.BIASEDCOIN_RANDOMIZATION.toString())) {
			randomisationConfigs.add(new AlgorithmWrapper(
					new BiasedCoinRandomizationConfig()));
		} else if (currentAlg.getSelectedAlgorithmPanelId().equals(
				Step5.AlgorithmPanelId.BLOCK_RANDOMIZATION.toString())) {
			AlgorithmWrapper algWrapper = new AlgorithmWrapper(
					new BlockRandomizationConfig());
			algWrapper.getBlockR().setLoginHandler(getLoginHandler());
			randomisationConfigs.add(algWrapper);
		} else if (currentAlg.getSelectedAlgorithmPanelId().equals(
				Step5.AlgorithmPanelId.TRUNCATED_RANDOMIZATION.toString())) {
			randomisationConfigs.add(new AlgorithmWrapper(
					new TruncatedBinomialDesignConfig()));
		} else if (currentAlg.getSelectedAlgorithmPanelId().equals(
				Step5.AlgorithmPanelId.URN_MODEL.toString())) {
			randomisationConfigs
					.add(new AlgorithmWrapper(new UrnDesignConfig()));
		} else if (currentAlg.getSelectedAlgorithmPanelId().equals(
				Step5.AlgorithmPanelId.MINIMIZATION.toString())) {
			randomisationConfigs.add(new AlgorithmWrapper(
					new MinimizationConfig()));
		}
		randomisationConfigs.get(randomisationConfigs.size() - 1).setPossition(
				randomisationConfigs.size() - 1);
		randomisationConfigs.get(randomisationConfigs.size() - 1).getBlockR()
				.setPossitionForSimulation(randomisationConfigs.size() - 1);
	}

	/**
	 * Action listener for removing an existing treatment arm.
	 * 
	 * @param event
	 */
	public void removeAlgorithm(ActionEvent event) {
		assert (showedObject != null);
		randomisationConfigs.remove(randomisationConfigs.size() - 1);
	}

	public void setCountTrialSites(int countTrialSites) {
		this.countTrialSites = countTrialSites;
		showedObject.setParticipatingSites(new HashSet<TrialSite>());
		for (int i = 0; i < countTrialSites; i++) {
			TrialSite site = new TrialSite();
			site.setName("Trial Site " + (i + 1));
			showedObject.addParticipatingSite(site);
		}
	}

	public String getExportSimulationResults() {
		StringBuffer sb = new StringBuffer();
		sb.append("<h2>Studie: "+ showedObject.getName() + "</h2> \n");
		
		sb.append("<h3>Sites:</h3>\n");
		sb.append("<table border=1 width=200px><tr><th>Name</th><th>Ratio</th></tr>");
		for (TrialSite site : showedObject.getParticipatingSites()) {
			sb.append("<tr><td>" + site.getName() + "</td><td>"+ 1 +"</td></tr> \n");
		}
		sb.append("</table>");
		
		sb.append("<h3>Treatment Arms:</h3>\n");
		sb.append("<table border=1 width=400px><tr><th>Name</th><th>Description</th><th>Ration</th></tr>\n");
		for (TreatmentArm arm : showedObject.getTreatmentArms()) {
			sb.append(" <tr><td> " + arm.getName() + "</td>");
			sb.append(" <td> "+ arm.getDescription() +"</td>");
			sb.append(" <td> " + arm.getPlannedSubjects() +"</td>"
					+ "</tr>\n");
		}
		sb.append("</table");
		
		sb.append("<h3>Simulation runs: </h3>" + runs + " \n");
		sb.append("<h3>Algorithms:</h3>\n");
		sb.append("<table border=1 width=800px>" +
				"<tr>" +
				"<th>Type</th><th>Time in ms</th><th>min Marginal Balance</th> <th>mean Marginal Balance</th> <th>max Marginal Balance</th>" +
				"</tr>");
		for (SimulationResult res : simulationResults) {
			sb.append(" <tr><td> "
					+ res.getAlgConf().getClass().getSimpleName() + "</td>");
			sb.append("<td> " + res.getDuration() + "</td>");
			sb.append("<td>"
					+ res.getMarginalBalanceMin() + "</td>");
			sb.append("<td>"
					+ res.getMarginalBalanceMean() + "</td>");
			sb.append("<td>"
					+ res.getMarginalBalanceMax() + "</td></tr>\n");
		}
		
		sb.append("</table>");
		sb.append("<table>");
		sb.append("<table border=0><tr><th><h3>Details</h3></th></tr>\n");
		for (SimulationResult res : simulationResults) {
			sb.append("<tr><th>"+res.getAlgConf().getClass().getSimpleName()+"</th></tr>");
			sb.append("<tr><td>");
			sb.append("<table border=1 width=800px><tr><th>Arm name</th><th>min</th>" +
					"<th>min per cent</th><th>max</th><th>max per cent</th><th>mean</th>" +
					"<th>median</th></tr>");
			for (SimualtionResultArm simArm : res.getSimResultArms()) {
				sb.append("<tr><td>" + simArm.getArm().getName() + "</td>");
				sb.append("<td>" + simArm.getMin() + "</td>");
				sb.append("<td>" + simArm.getMinPercentString() + "</td>");
				sb.append("<td>" + simArm.getMax() + "</td>");
				sb.append("<td>" + simArm.getMaxPercentString() + "</td>");
				sb.append("<td>" + simArm.getMean() + "</td>");
				sb.append("<td>" + simArm.getMedian() + "</td></tr>\n");
			}
			sb.append("</table>");
			sb.append("</td></tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
}