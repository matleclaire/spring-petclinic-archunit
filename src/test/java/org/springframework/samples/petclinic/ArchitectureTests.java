package org.springframework.samples.petclinic;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;

@AnalyzeClasses(packagesOf = PetClinicApplication.class, importOptions = DoNotIncludeTests.class)
class ArchitectureTests {
}
