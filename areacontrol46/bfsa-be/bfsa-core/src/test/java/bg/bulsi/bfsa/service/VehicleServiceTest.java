package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Vehicle;
import bg.bulsi.bfsa.model.VehicleI18n;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

@Slf4j
class VehicleServiceTest extends BaseServiceTest {
    @Autowired
    private VehicleService service;
    @Autowired
    private BranchBuilder branchBuilder;

    @Test
    void givenVehicle_whenCreate_thenReturnVehicle() {
        Vehicle vehicle = mockVehicle(branchBuilder.saveBranch(langBG));
        Assertions.assertNotNull(vehicle);

        Vehicle saved = service.create(vehicle);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(vehicle.getEnabled(), saved.getEnabled());
        Assertions.assertEquals(vehicle.getCertificateNumber(), saved.getCertificateNumber());
        Assertions.assertEquals(vehicle.getCertificateDate(), saved.getCertificateDate());
        Assertions.assertEquals(vehicle.getEntryNumber(), saved.getEntryNumber());
        Assertions.assertEquals(vehicle.getEntryDate(), saved.getEntryDate());
        Assertions.assertEquals(vehicle.getBranch().getId(), saved.getBranch().getId());
//        If we need Settlement at this level (here) remove FetchType.LAZY from OneToOne Settlement relation in Branch entity
//        Assertions.assertEquals(vehicle.getBranch().getSettlement().getName(), saved.getBranch().getSettlement().getName());
        Assertions.assertEquals(vehicle.getI18n(langBG).getDescription(), saved.getI18n(langBG).getDescription());
    }

    @Test
    void givenSomeVehicles_whenFindAll_thenReturnPageOfAllVehicles() {
        Vehicle saved0 = service.create(mockVehicle(branchBuilder.saveBranch(langBG)));
        Assertions.assertNotNull(saved0);

        Vehicle saved1 = service.create(mockVehicle(branchBuilder.saveBranch(langBG)));
        Assertions.assertNotNull(saved1);

        Page<VehicleDTO> saved = service.findAll(Pageable.unpaged(), langBG);

        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getContent());
        Assertions.assertTrue(saved.getContent().size() > 1);
    }

    @Test
    void givenId_whenFinaById_thenReturnVehicle() {
        Vehicle mockVehicle = mockVehicle(branchBuilder.saveBranch(langBG));
        Assertions.assertNotNull(mockVehicle);

        VehicleDTO saved = VehicleDTO.of(service.create(mockVehicle), langBG);
        Assertions.assertNotNull(saved);

        VehicleDTO found = service.getById(saved.getId(), langBG);

        Assertions.assertNotNull(found);
        Assertions.assertEquals(saved.getId(), found.getId());
        Assertions.assertEquals(saved.getEnabled(), found.getEnabled());
        Assertions.assertEquals(saved.getCertificateNumber(), found.getCertificateNumber());
        Assertions.assertEquals(saved.getCertificateDate(), found.getCertificateDate());
        Assertions.assertEquals(saved.getEntryNumber(), found.getEntryNumber());
        Assertions.assertEquals(saved.getEntryDate(), found.getEntryDate());
        Assertions.assertEquals(saved.getDescription(), found.getDescription());
    }

    @Test
    void givenVehicle_whenUpdate_thenReturnUpdatedVehicle() {
        Vehicle mockVehicle = mockVehicle(branchBuilder.saveBranch(langBG));
        Assertions.assertNotNull(mockVehicle);

        VehicleDTO created = VehicleDTO.of(service.create(mockVehicle), langBG);
        Assertions.assertNotNull(created);

        created.setBrandModel(created.getBranchId() + "_Updated");
        created.setDescription(created.getDescription() + "_Updated");

        VehicleDTO updated = service.update(created.getId(), VehicleDTO.to(created, langBG), langBG);

        Assertions.assertNotNull(updated);
        Assertions.assertEquals(created.getId(), updated.getId());
        Assertions.assertEquals(created.getEnabled(), updated.getEnabled());
        Assertions.assertEquals(created.getCertificateNumber(), updated.getCertificateNumber());
        Assertions.assertEquals(created.getCertificateDate(), updated.getCertificateDate());
        Assertions.assertEquals(created.getEntryNumber(), updated.getEntryNumber());
        Assertions.assertEquals(created.getEntryDate(), updated.getEntryDate());
        Assertions.assertEquals(created.getBranchId(), updated.getBranchId());
        Assertions.assertEquals(created.getDescription(), updated.getDescription());
    }

    @Test
    void givenFakeId_whenUpdate_thenThrowsEntityNotFoundException() {
        Long fakeId = new Random().nextLong(1000);
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> service.update(
                        fakeId, Vehicle.builder().id(fakeId).build(), langEN)
        );

        Assertions.assertEquals("Entity '"
                        + Vehicle.class.getName() + "' with id/code='"
                        + fakeId + "' not found",
                thrown.getMessage());
    }

    @Test
    void givenString_whenSearch_thenReturnFoodSupplement() {
        VehicleDTO saved0 = VehicleDTO.of(service.create(mockVehicle(
                "XXX",
                "YYY",
                branchBuilder.saveBranch(langBG))
        ), langBG);
        Assertions.assertNotNull(saved0);

        VehicleDTO saved1 = VehicleDTO.of(service.create(mockVehicle(
                "YYY",
                "ZZZ",
                branchBuilder.saveBranch(langBG)
        )), langBG);
        Assertions.assertNotNull(saved1);

        Page<Vehicle> found = service.search("XXX", langBG, Pageable.unpaged());

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.getTotalElements());
        Assertions.assertEquals(saved0.getEntryNumber(), found.getContent().get(0).getEntryNumber());
        Assertions.assertEquals(saved0.getCertificateNumber(), found.getContent().get(0).getCertificateNumber());
        Assertions.assertEquals(saved0.getCertificateDate(), found.getContent().get(0).getCertificateDate());
        Assertions.assertEquals(saved0.getEntryNumber(), found.getContent().get(0).getEntryNumber());
        Assertions.assertEquals(saved0.getEntryDate(), found.getContent().get(0).getEntryDate());
        Assertions.assertEquals(saved0.getDescription(), found.getContent().get(0).getI18n(langBG).getDescription());

        found = service.search("ZZZ", langBG, Pageable.unpaged());

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.getTotalElements());
        Assertions.assertEquals(saved1.getEntryNumber(), found.getContent().get(0).getEntryNumber());
        Assertions.assertEquals(saved1.getCertificateNumber(), found.getContent().get(0).getCertificateNumber());
        Assertions.assertEquals(saved1.getCertificateDate(), found.getContent().get(0).getCertificateDate());
        Assertions.assertEquals(saved1.getEntryNumber(), found.getContent().get(0).getEntryNumber());
        Assertions.assertEquals(saved1.getEntryDate(), found.getContent().get(0).getEntryDate());
        Assertions.assertEquals(saved1.getDescription(), found.getContent().get(0).getI18n(langBG).getDescription());

        found = service.search("YYY", langBG, Pageable.unpaged());

        Assertions.assertNotNull(found);
        Assertions.assertEquals(2, found.getTotalElements());

    }

    private Vehicle mockVehicle(final Branch branch) {
        return mockVehicle(null, null, branch);
    }

    private Vehicle mockVehicle(final String entryNumber, final String brandModel, final Branch branch) {
        Vehicle vehicle = Vehicle.builder()
                .registrationPlate(UUID.randomUUID().toString())
                .entryNumber(StringUtils.hasText(entryNumber) ? entryNumber : UUID.randomUUID().toString())
                .entryDate(LocalDate.now())
                .certificateNumber(UUID.randomUUID().toString())
                .certificateDate(LocalDate.now())
                .load(Math.random())
                .enabled(true)
                .branch(branch)
                .build();
        vehicle.getI18ns().add(
                new VehicleI18n(
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        brandModel,
                        vehicle,
                        langBG
                )
        );
        return vehicle;
    }
}