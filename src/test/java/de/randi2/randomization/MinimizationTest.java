package de.randi2.randomization;

import static de.randi2.randomization.RandomizationHelper.randomize;
import static de.randi2.utility.IntegerIterator.upto;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import de.randi2.model.SubjectProperty;
import de.randi2.model.TreatmentArm;
import de.randi2.model.Trial;
import de.randi2.model.TrialSubject;
import de.randi2.model.criteria.DichotomousCriterion;
import de.randi2.model.criteria.OrdinalCriterion;
import de.randi2.model.criteria.constraints.DichotomousConstraint;
import de.randi2.model.criteria.constraints.OrdinalConstraint;
import de.randi2.model.randomization.MinimizationConfig;
import de.randi2.simulation.distribution.UniformDistribution;
import de.randi2.simulation.model.DistributionSubjectProperty;
import de.randi2.unsorted.ContraintViolatedException;

public class MinimizationTest {

	private Trial trial;
	private TrialSubject s;
	private MinimizationConfig conf;
	private int id = 0;

	
	private int nextId(){
		return id++;
	}
	
	@Before
	public void setUp() {
		trial = new Trial();
		
	}


	@Test
	public void testNaiveMinimization(){
		RandomizationHelper.addArms(trial, 70,50,30);
		conf = new MinimizationConfig();
		conf.setWithRandomizedSubjects(false);
		conf.setP(0.95);
		trial.setRandomizationConfiguration(conf);
		for (int i : upto(150)){
			randomize(trial, new TrialSubject());
		}
		assertEquals(150, trial.getSubjects().size());
		
	}
	
	@Test
	public void testNaiveMinimizationWithRandomizedSubjects(){
		RandomizationHelper.addArms(trial, 70,50,30);
		conf = new MinimizationConfig();
		conf.setWithRandomizedSubjects(true);
		conf.setP(0.9);
		trial.setRandomizationConfiguration(conf);
		for (int i : upto(150)){
			randomize(trial, new TrialSubject());
		}
		assertEquals(150, trial.getSubjects().size());
	}	
	
	@Test
	public void testNaiveMinimizationWithRandomizedSubjects_5_Treatments(){
		RandomizationHelper.addArms(trial, 56,23,78,47,29);
		conf = new MinimizationConfig();
		conf.setWithRandomizedSubjects(true);
		conf.setP(0.95);
		trial.setRandomizationConfiguration(conf);
		for (int i : upto(233)){
			randomize(trial, new TrialSubject());
		}
		assertEquals(233, trial.getSubjects().size());
	}
	
	
	@Test
	public void testBiasedCoinMinimization(){
		RandomizationHelper.addArms(trial, 10,20,30);
		trial.setId(nextId());
		
		for(TreatmentArm arm :trial.getTreatmentArms())	arm.setId(nextId());
		
		ArrayList<DistributionSubjectProperty> dProperties = new ArrayList<DistributionSubjectProperty>();
		DichotomousCriterion cr1 = new DichotomousCriterion();
		cr1.setName("SEX");
		cr1.setOption1("M");
		cr1.setOption2("F");
		cr1.setId(nextId());
		OrdinalCriterion cr2 = new OrdinalCriterion();
		List<String> elements = new ArrayList<String>();
		elements.add("1");
		elements.add("2");
		elements.add("3");
		elements.add("4");
		elements.add("5");
		cr2.setElements(elements);
		cr2.setName("TrialSite");
		cr2.setId(nextId());
		try {
			
			cr1.addStrata(new DichotomousConstraint(Arrays
					.asList(new String[] { "M" })));

			cr1.addStrata(new DichotomousConstraint(Arrays
					.asList(new String[] { "F" })));
			
		 
			
			cr2.addStrata(new OrdinalConstraint(Arrays
					.asList(new String[] { "1" })));
			cr2.addStrata(new OrdinalConstraint(Arrays
					.asList(new String[] { "2" })));
			cr2.addStrata(new OrdinalConstraint(Arrays
					.asList(new String[] { "3" })));
			cr2.addStrata(new OrdinalConstraint(Arrays
					.asList(new String[] { "4" })));
			cr2.addStrata(new OrdinalConstraint(Arrays
					.asList(new String[] { "5" })));
			

			for(DichotomousConstraint cc : cr1.getStrata())	cc.setId(nextId());
			for(OrdinalConstraint cc : cr2.getStrata()) cc.setId(nextId());
			
			dProperties.add(new DistributionSubjectProperty(cr1,  new UniformDistribution<String>(cr1.getConfiguredValues(),1)));
			dProperties.add(new DistributionSubjectProperty(cr2,  new UniformDistribution<String>(cr2.getConfiguredValues(),1)));

			
			
		} catch (ContraintViolatedException e) {
			e.printStackTrace();
		}
		
		trial.addCriterion(cr1);
		trial.addCriterion(cr2);
		Minimization algorithm = new Minimization(trial, 1, 1);
		conf = new MinimizationConfig();
		conf.setWithRandomizedSubjects(false);
		conf.setBiasedCoinMinimization(true);
		conf.setP(0.70);
		algorithm.configuration = conf;
		trial.setRandomizationConfiguration(conf);
		for (int i : upto(60)){
			TrialSubject subject = new TrialSubject();
			TreatmentArm assignedArm = algorithm.randomize(generateTrialSubject(dProperties, subject));
			subject.setArm(assignedArm);
			assignedArm.addSubject(subject);
			System.out.println(assignedArm.getName());
			System.out.println("--");
		}
//		for(TreatmentArm arm : trial.getTreatmentArms()){
//			System.out.println(arm.getName() + " size: " + arm.getSubjects().size());	
//		}
		
		assertEquals(60, trial.getSubjects().size());
	}
	
	
	private static TrialSubject generateTrialSubject(List<DistributionSubjectProperty> properties, TrialSubject oldSubject) {
		oldSubject.setProperties(null);
		HashSet<SubjectProperty<?>> tempSet = new HashSet<SubjectProperty<?>>();
		for (DistributionSubjectProperty dsp :properties) {
			SubjectProperty<Serializable> pr = new SubjectProperty<Serializable>(
					dsp.getCriterion());
			try {
				pr.setValue(dsp.getNextSubjectValue());
				System.out.println(pr.getValue());
			} catch (ContraintViolatedException e) {
			}
			tempSet.add(pr);
		}
		oldSubject.setProperties(tempSet);
		return oldSubject;
	}
	
	
	@Test
	// Minimization_Prop_Table (1).pdf
	public void testProbabilityInitBiasedCoin1(){
		RandomizationHelper.addArms(trial, 10,20,30);
		List<TreatmentArm> arms = trial.getTreatmentArms();
		Minimization algorithm = new Minimization(trial, 1);
		conf = new MinimizationConfig();
		conf.setWithRandomizedSubjects(false);
		conf.setBiasedCoinMinimization(true);
		conf.setP(0.70);
		algorithm.configuration = conf;
		trial.setRandomizationConfiguration(conf);
		Minimization alg = (Minimization)conf.getAlgorithm();
		Map<TreatmentArm, Map<TreatmentArm, Double>> internalCalculatedProbabilities = alg.getProbabilitiesPerPreferredTreatment();
		
		Map<TreatmentArm, Map<TreatmentArm, Double>> externelCalculatedProbabilies = new HashMap<TreatmentArm, Map<TreatmentArm,Double>>();
		
		Map<TreatmentArm, Double> calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.7);
		calculatedProbabilies.put(arms.get(1), 0.12);
		calculatedProbabilies.put(arms.get(2), 0.18);
		externelCalculatedProbabilies.put(arms.get(0), calculatedProbabilies);
		
		calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.06);
		calculatedProbabilies.put(arms.get(1), 0.76);
		calculatedProbabilies.put(arms.get(2), 0.18);
		externelCalculatedProbabilies.put(arms.get(1), calculatedProbabilies);
		
		calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.06);
		calculatedProbabilies.put(arms.get(1), 0.12);
		calculatedProbabilies.put(arms.get(2), 0.82);
		externelCalculatedProbabilies.put(arms.get(2), calculatedProbabilies);
		testProbabilityMaps(externelCalculatedProbabilies, internalCalculatedProbabilities);
		
	}
	
	@Test
	// Minimization_Prop_Table (2).pdf
	public void testProbabilityInitBiasedCoin2(){
		RandomizationHelper.addArms(trial, 10,20,30);
		List<TreatmentArm> arms = trial.getTreatmentArms();
		Minimization algorithm = new Minimization(trial, 1);
		conf = new MinimizationConfig();
		conf.setWithRandomizedSubjects(false);
		conf.setBiasedCoinMinimization(true);
		conf.setP(0.80);
		algorithm.configuration = conf;
		trial.setRandomizationConfiguration(conf);
		Minimization alg = (Minimization)conf.getAlgorithm();
		Map<TreatmentArm, Map<TreatmentArm, Double>> internalCalculatedProbabilities = alg.getProbabilitiesPerPreferredTreatment();
		
		Map<TreatmentArm, Map<TreatmentArm, Double>> externelCalculatedProbabilies = new HashMap<TreatmentArm, Map<TreatmentArm,Double>>();
		
		Map<TreatmentArm, Double> calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.8);
		calculatedProbabilies.put(arms.get(1), 0.08);
		calculatedProbabilies.put(arms.get(2), 0.12);
		externelCalculatedProbabilies.put(arms.get(0), calculatedProbabilies);
		
		calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.04);
		calculatedProbabilies.put(arms.get(1), 0.84);
		calculatedProbabilies.put(arms.get(2), 0.12);
		externelCalculatedProbabilies.put(arms.get(1), calculatedProbabilies);
		
		calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.04);
		calculatedProbabilies.put(arms.get(1), 0.08);
		calculatedProbabilies.put(arms.get(2), 0.88);
		externelCalculatedProbabilies.put(arms.get(2), calculatedProbabilies);
		testProbabilityMaps(externelCalculatedProbabilies, internalCalculatedProbabilities);
		
	}
	
	@Test
	// Minimization_Prop_Table (3).pdf
	public void testProbabilityInitBiasedCoin3(){
		RandomizationHelper.addArms(trial, 10,20,30);
		List<TreatmentArm> arms = trial.getTreatmentArms();
		Minimization algorithm = new Minimization(trial, 1);
		conf = new MinimizationConfig();
		conf.setWithRandomizedSubjects(false);
		conf.setBiasedCoinMinimization(true);
		conf.setP(0.60);
		algorithm.configuration = conf;
		trial.setRandomizationConfiguration(conf);
		Minimization alg = (Minimization)conf.getAlgorithm();
		Map<TreatmentArm, Map<TreatmentArm, Double>> internalCalculatedProbabilities = alg.getProbabilitiesPerPreferredTreatment();
		
		Map<TreatmentArm, Map<TreatmentArm, Double>> externelCalculatedProbabilies = new HashMap<TreatmentArm, Map<TreatmentArm,Double>>();
		
		Map<TreatmentArm, Double> calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.6);
		calculatedProbabilies.put(arms.get(1), 0.16);
		calculatedProbabilies.put(arms.get(2), 0.24);
		externelCalculatedProbabilies.put(arms.get(0), calculatedProbabilies);
		
		calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.08);
		calculatedProbabilies.put(arms.get(1), 0.68);
		calculatedProbabilies.put(arms.get(2), 0.24);
		externelCalculatedProbabilies.put(arms.get(1), calculatedProbabilies);
		
		calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.08);
		calculatedProbabilies.put(arms.get(1), 0.16);
		calculatedProbabilies.put(arms.get(2), 0.76);
		externelCalculatedProbabilies.put(arms.get(2), calculatedProbabilies);
		testProbabilityMaps(externelCalculatedProbabilies, internalCalculatedProbabilities);
	}
	
	
	@Test
	// Minimization_Prop_Table (4).pdf
	public void testProbabilityInitBiasedCoin4(){
		RandomizationHelper.addArms(trial, 10,10,10);
		List<TreatmentArm> arms = trial.getTreatmentArms();
		Minimization algorithm = new Minimization(trial, 1);
		conf = new MinimizationConfig();
		conf.setWithRandomizedSubjects(false);
		conf.setBiasedCoinMinimization(true);
		conf.setP(0.60);
		algorithm.configuration = conf;
		trial.setRandomizationConfiguration(conf);
		Minimization alg = (Minimization)conf.getAlgorithm();
		Map<TreatmentArm, Map<TreatmentArm, Double>> internalCalculatedProbabilities = alg.getProbabilitiesPerPreferredTreatment();
		
		Map<TreatmentArm, Map<TreatmentArm, Double>> externelCalculatedProbabilies = new HashMap<TreatmentArm, Map<TreatmentArm,Double>>();
		
		Map<TreatmentArm, Double> calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.6);
		calculatedProbabilies.put(arms.get(1), 0.2);
		calculatedProbabilies.put(arms.get(2), 0.2);
		externelCalculatedProbabilies.put(arms.get(0), calculatedProbabilies);
		
		calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.2);
		calculatedProbabilies.put(arms.get(1), 0.6);
		calculatedProbabilies.put(arms.get(2), 0.2);
		externelCalculatedProbabilies.put(arms.get(1), calculatedProbabilies);
		
		calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.2);
		calculatedProbabilies.put(arms.get(1), 0.2);
		calculatedProbabilies.put(arms.get(2), 0.6);
		externelCalculatedProbabilies.put(arms.get(2), calculatedProbabilies);
		testProbabilityMaps(externelCalculatedProbabilies, internalCalculatedProbabilities);
	}
	
	
	@Test
	// Minimization_Prop_Table (5).pdf
	public void testProbabilityInitBiasedCoin5(){
		RandomizationHelper.addArms(trial, 1,2,2,4);
		List<TreatmentArm> arms = trial.getTreatmentArms();
		Minimization algorithm = new Minimization(trial, 1);
		conf = new MinimizationConfig();
		conf.setWithRandomizedSubjects(false);
		conf.setBiasedCoinMinimization(true);
		conf.setP(0.80);
		algorithm.configuration = conf;
		trial.setRandomizationConfiguration(conf);
		Minimization alg = (Minimization)conf.getAlgorithm();
		Map<TreatmentArm, Map<TreatmentArm, Double>> internalCalculatedProbabilities = alg.getProbabilitiesPerPreferredTreatment();
		
		Map<TreatmentArm, Map<TreatmentArm, Double>> externelCalculatedProbabilies = new HashMap<TreatmentArm, Map<TreatmentArm,Double>>();
		
		Map<TreatmentArm, Double> calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.8);
		calculatedProbabilies.put(arms.get(1), 0.05);
		calculatedProbabilies.put(arms.get(2), 0.05);
		calculatedProbabilies.put(arms.get(3), 0.1);
		externelCalculatedProbabilies.put(arms.get(0), calculatedProbabilies);
		
		calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.025);
		calculatedProbabilies.put(arms.get(1), 0.825);
		calculatedProbabilies.put(arms.get(2), 0.05);
		calculatedProbabilies.put(arms.get(3), 0.1);
		externelCalculatedProbabilies.put(arms.get(1), calculatedProbabilies);
		
		calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.025);
		calculatedProbabilies.put(arms.get(1), 0.05);
		calculatedProbabilies.put(arms.get(2), 0.825);
		calculatedProbabilies.put(arms.get(3), 0.1);
		externelCalculatedProbabilies.put(arms.get(2), calculatedProbabilies);
		testProbabilityMaps(externelCalculatedProbabilies, internalCalculatedProbabilities);
		
		calculatedProbabilies = new HashMap<TreatmentArm,Double>();
		calculatedProbabilies.put(arms.get(0), 0.025);
		calculatedProbabilies.put(arms.get(1), 0.05);
		calculatedProbabilies.put(arms.get(2), 0.05);
		calculatedProbabilies.put(arms.get(3), 0.875);
		externelCalculatedProbabilies.put(arms.get(3), calculatedProbabilies);
		testProbabilityMaps(externelCalculatedProbabilies, internalCalculatedProbabilities);
	}
	
	
	@Test
	public void testProbabilityInitBiasedCoinSum_1(){
		RandomizationHelper.addArms(trial, 1,2,2,4);
		List<TreatmentArm> arms = trial.getTreatmentArms();
		Minimization algorithm = new Minimization(trial, 1);
		conf = new MinimizationConfig();
		conf.setWithRandomizedSubjects(false);
		conf.setBiasedCoinMinimization(true);
		conf.setP(0.80);
		algorithm.configuration = conf;
		trial.setRandomizationConfiguration(conf);
		Minimization alg = (Minimization)conf.getAlgorithm();
		Map<TreatmentArm, Map<TreatmentArm, Double>> internalCalculatedProbabilities = alg.getProbabilitiesPerPreferredTreatment();
		testSumEquals1(internalCalculatedProbabilities);
	}
	
	private void testSumEquals1(Map<TreatmentArm, Map<TreatmentArm, Double>> internalCalculatedProbabilities){
		DecimalFormat df = new DecimalFormat("#0.0000000000");
		for(TreatmentArm arm_pref : internalCalculatedProbabilities.keySet()){
			Map<TreatmentArm, Double> prob_calc = internalCalculatedProbabilities.get(arm_pref);
			double sum = 0.0;
			for(TreatmentArm arm_act : prob_calc.keySet()){
				sum+=internalCalculatedProbabilities.get(arm_pref).get(arm_act);
			}
			assertEquals(df.format(1.0),df.format(sum));
		}
	}
	private void testProbabilityMaps(Map<TreatmentArm, Map<TreatmentArm, Double>> externelCalculatedProbabilies, Map<TreatmentArm, Map<TreatmentArm, Double>> internalCalculatedProbabilities){
		DecimalFormat df = new DecimalFormat("#0.0000000000");
		testSumEquals1(internalCalculatedProbabilities);
		for(TreatmentArm arm_pref : externelCalculatedProbabilies.keySet()){
			Map<TreatmentArm, Double> prob_calc = externelCalculatedProbabilies.get(arm_pref);
			for(TreatmentArm arm_act : prob_calc.keySet()){
				assertEquals(df.format(externelCalculatedProbabilies.get(arm_pref).get(arm_act)), df.format(internalCalculatedProbabilities.get(arm_pref).get(arm_act)));
			}
		}
	}
}
