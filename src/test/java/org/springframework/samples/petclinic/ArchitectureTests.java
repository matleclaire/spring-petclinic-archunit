package org.springframework.samples.petclinic;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import org.springframework.stereotype.Controller;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.conditions.ArchConditions.dependOnClassesThat;
import static com.tngtech.archunit.lang.conditions.ArchConditions.not;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packagesOf = PetClinicApplication.class, importOptions = DoNotIncludeTests.class)
class ArchitectureTests {

	@ArchTest
	public static final ArchRule CONTROLLER_NAMING = classes()
		.that().areAnnotatedWith(Controller.class)
		.or().haveSimpleNameEndingWith("Controller")
		.should().beAnnotatedWith(Controller.class)
		.andShould().haveSimpleNameEndingWith("Controller")
		.because("controller should be easy to find");


	@ArchTest
	public static final ArchRule DEPENDENCIES_BETWEEN_MODULES = CompositeArchRule
		.of(
			classes()
				.that().resideInAPackage("..owner..")
				.should(not(dependOnClassesThat(resideInAPackage("..vet..")))))
		.and(
			classes()
				.that().resideInAPackage("..vet..")
				.should(not(dependOnClassesThat(resideInAPackage("..owner..")))));

}
