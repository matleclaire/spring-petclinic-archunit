package org.springframework.samples.petclinic;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.library.freeze.FreezingArchRule;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.logging.Logger;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.ConditionEvent.createMessage;
import static com.tngtech.archunit.lang.SimpleConditionEvent.satisfied;
import static com.tngtech.archunit.lang.SimpleConditionEvent.violated;
import static com.tngtech.archunit.lang.conditions.ArchConditions.dependOnClassesThat;
import static com.tngtech.archunit.lang.conditions.ArchConditions.not;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

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


	@ArchTest
	public static final ArchRule CONTROLLER_SHOULD_LOG = freeze(methods()
		.that().areAnnotatedWith(PostMapping.class)
		.should(log()));

	private static ArchCondition<? super JavaMethod> log() {
		return new ArchCondition<>("log") {
			@Override
			public void check(JavaMethod item, ConditionEvents events) {
				List<JavaMethodCall> loggingCalls = item.getMethodCallsFromSelf().stream()
					.filter(call -> call.getTargetOwner().isEquivalentTo(Logger.class))
					.toList();
				if (loggingCalls.isEmpty()) {
					events.add(violated(item, createMessage(item, "does not log")));
				} else {
					for (JavaMethodCall loggingCall : loggingCalls) {
						events.add(satisfied(item, loggingCall.getDescription()));
					}
				}
			}
		};
	}

}
