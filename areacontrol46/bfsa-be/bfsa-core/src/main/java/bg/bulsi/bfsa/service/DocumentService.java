package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.custom.DescribedByteArrayResource;
import bg.bulsi.bfsa.dto.AddressBO;
import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.AdjuvantBO;
import bg.bulsi.bfsa.dto.ApplicationS2274ActiveSubstanceDTO;
import bg.bulsi.bfsa.dto.ApplicationS2274PackageDTO;
import bg.bulsi.bfsa.dto.ApplicationS2274ReferenceProductDTO;
import bg.bulsi.bfsa.dto.CropBO;
import bg.bulsi.bfsa.dto.CropDoseBO;
import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.dto.IngredientBO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.PersonBO;
import bg.bulsi.bfsa.dto.SubstanceBO;
import bg.bulsi.bfsa.enums.EducationalDocumentType;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS1366;
import bg.bulsi.bfsa.model.ApplicationS1366Product;
import bg.bulsi.bfsa.model.ApplicationS1366ProductBatch;
import bg.bulsi.bfsa.model.ApplicationS2170;
import bg.bulsi.bfsa.model.ApplicationS2274;
import bg.bulsi.bfsa.model.ApplicationS2695;
import bg.bulsi.bfsa.model.ApplicationS2695Ppp;
import bg.bulsi.bfsa.model.ApplicationS2697;
import bg.bulsi.bfsa.model.ApplicationS2698;
import bg.bulsi.bfsa.model.ApplicationS2701;
import bg.bulsi.bfsa.model.ApplicationS502;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.FoodSupplement;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.util.Constants;
import bg.bulsi.bfsa.util.ImageValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.FontFamily;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DocumentService {

    private final RecordService recordService;
    private final NomenclatureService nomenclatureService;

    @Value("templates/document/")
    private String baseTemplatePath;

    private final String MULTIPLE_SEND_TEXT_BG = "Брой на изпращанията и количество посочен материал за всяко изпращане:";
    private final String MULTIPLE_SEND_TEXT_EN = "Number of sending and quantity per sending of specified material:";
    private final String MULTIPLE_SEND_TYPE_TEXT_BG = "Еднократна доставка";
    private final String MULTIPLE_SEND_TYPE_TEXT_EN = "One time shipment";
    private final String LANG_BG = "bg";
    private final String WEIGHT_BG = "Тегло: ";
    private final String WEIGHT_EN = "Weight: ";
    private final String PHONE_BG = "Тел.: ";
    private final String PHONE_EN = "Phone: ";
    private final String EMAIL_BG = "Ел. поща: ";
    private final String EMAIL_EN = "E-mail: ";
    private final String ENDORSEMENT_TEXT_BG = "Подпис и печат или електронен печат и електронен подпис на компетентния орган";
    private final String ENDORSEMENT_TEXT_EN = "Signature and stamp, or electronic stamp and electronic signature of the Competent Authority";
    private final String ENDORSEMENT_PLACE_BG = "Място на заверка:";
    private final String ENDORSEMENT_PLACE_EN = "Place of endorsement:";
    private final String ENDORSEMENT_DATE_BG = "Дата:";
    private final String ENDORSEMENT_DATE_EN = "Date:";
    private final String ENDORSEMENT_SIGNATURE_BG = "Име и подпис на упълномощения служител:";
    private final String ENDORSEMENT_SIGNATURE_EN = "Name and signature of the authorised officer:";
    private final String IMSOC_BG = "Референтен номер в IMSOC:";
    private final String IMSOC_EN = "IMSOC reference:";
    private final String DOTS = "..........";

    @Transactional(readOnly = true)
    public Resource exportPaper(final Long recordId, Language language) {
        Record record = recordService.findById(recordId);
        XSSFWorkbook wb;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        String name = "certificate-" + record.getServiceType() + ".xlsx";
        try {
            wb = new XSSFWorkbook(new ClassPathResource(baseTemplatePath + name).getInputStream());
            switch (record.getServiceType()) {
                case S502:
                    applicationS502(wb.getSheetAt(0), record, language);
                    break;
                case S503:
                    applicationS503(wb.getSheetAt(0), record, language);
                    break;
                case S1366:
                    applicationS1366(wb, record, language);
                    break;
                case S2170:
                    applicationS2170(wb.getSheetAt(0), record, language);
                    break;
                case S2274:
                    applicationS2274(wb.getSheetAt(0), record, language);
                    break;
                case S2695:
                    applicationS2695(wb.getSheetAt(0), record, language);
                    break;
                case S2697:
                    applicationS2697(wb.getSheetAt(0), record, language);
                    break;
                case S2701:
                    applicationS2701(wb.getSheetAt(0), record, language);
                    break;
                case S2702:
                    applicationS2702(wb.getSheetAt(0), record, language);
                    break;
                case S2711:
                    applicationS2711(wb.getSheetAt(0), record, language);
                    break;
                case S2869:
                    applicationS2869(wb.getSheetAt(0), record, language);
                    break;
                case S3201:
                    applicationS3201(wb.getSheetAt(0), record, language);
                    break;
                case S3180:
                    applicationS3180(wb.getSheetAt(0), record, language);
                    break;
            }
            wb.write(bos);
            return new DescribedByteArrayResource(bos.toByteArray(), name);
        } catch (IOException e) {
            log.error("Error exporting document: " + name, e);
            throw new RuntimeException("Can't process template: " + name);
        }
    }

    @Transactional(readOnly = true)
    public Resource exportOrder(final Long recordId, Language language) {
        Record record = recordService.findById(recordId);
        XSSFWorkbook wb;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        String name = "order-" + record.getServiceType() + ".xlsx";
        try {
            wb = new XSSFWorkbook(new ClassPathResource(baseTemplatePath + name).getInputStream());
            switch (record.getServiceType()) {
                case S502:
                    orderS502(wb.getSheetAt(0), record, language);
                    break;
                case S1590, S2699, S2700:
                    orderS1590S2699S2700(wb.getSheetAt(0), record, language);
                    break;
                case S2274:
                    orderS2274(wb.getSheetAt(0), record, language);
                    break;
                case S2698:
                    orderS2698(wb.getSheetAt(0), record, language);
                    break;
                case S3125:
                    refusalOrderS3125(wb.getSheetAt(0), record, language);
                    break;
                case S3180:
                    refusalOrderS3180(wb.getSheetAt(0), record, language);
                    break;
                case S7691, S7692, S7693, S7695:
                    orderS7691S7692S7693S7695(wb.getSheetAt(0), record, record.getServiceType(), language);
                    break;
                case S7694:
                    orderS7694(wb.getSheetAt(0), record, language);
                    break;
            }
            wb.write(bos);
            return new DescribedByteArrayResource(bos.toByteArray(), name);
        } catch (IOException e) {
            log.error("Error exporting document: " + name, e);
            throw new RuntimeException("Can't process template: " + name);
        }
    }

    /**
     * Базова логика за извличане на JSON от PDF файла.
     *
     * @return
     */
    public String getPdfDataJson(byte[] fileContent, final String metadataKey) {
        try (PDDocument document = Loader.loadPDF(fileContent)) {
            Set<String> keys = document.getDocumentInformation().getMetadataKeys();
            if (keys != null) {
                for (String key : keys) {
                    if (key.equals(metadataKey)) {
                        return document.getDocumentInformation().getCustomMetadataValue(key);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error getting pdf data json", e);
        }

        return null;
    }

    private void applicationS502(XSSFSheet sheet, Record record, Language language) {

        String paperNumber = "№ от деловодна система";
        String certValid = "2023-11-28";
        if (record.getContractorPaper() != null) {
            paperNumber = StringUtils.hasText(record.getContractorPaper().getRegNumber())
                    ? record.getContractorPaper().getRegNumber()
                    : "№ от деловодна система"; // TODO: Remevo when get correct number
            certValid = record.getContractorPaper().getValidUntilDate() + "г.";
        }
        sheet.getRow(10).getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(paperNumber);

        sheet.getRow(17).getCell(5).setCellValue(record.getApplicant().getFullName());

        Address address = record.getApplicant().getAddresses().stream()
                .filter(a -> ServiceType.S502.equals(a.getServiceType())
                        && Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE.equals(a.getAddressType().getCode()))
                .findFirst().orElse(null);

        String fullAddress = "Липсва адрес";
        if (address != null) {
            fullAddress = address.getFullAddress();
        }
        sheet.getRow(20).getCell(5).setCellValue(fullAddress);

        sheet.getRow(22).getCell(4).setCellValue(record.getApplicant().getIdentifier());

        ApplicationS502 application = record.getApplicationS502();
        List<AddressBO> testingAddresses = application.getTestingAddresses();
        StringBuilder sbAddresses = new StringBuilder();
        for (AddressBO testingAddress : testingAddresses) {
            sbAddresses.append(testingAddress.getFullAddress()).append("\n");
        }
        sheet.getRow(25).getCell(4).setCellValue(sbAddresses.toString().trim());
        if (testingAddresses.size() > 1) {
            sheet.getRow(25).setHeightInPoints(testingAddresses.size() * 25f);
        }

        List<Nomenclature> groupTypes = application.getPlantGroupTypes();
        StringBuilder sbGroupTypes = new StringBuilder();
        for (int i = 1; i <= groupTypes.size(); i++) {
            sbGroupTypes.append(i)
                    .append(". ")
                    .append(groupTypes.get(i - 1).getI18n(language).getName())
                    .append("\n");
        }
        sheet.getRow(28).getCell(2).setCellValue(sbGroupTypes.toString().trim());
        if (groupTypes.size() > 1) {
            sheet.getRow(28).setHeightInPoints(groupTypes.size() * 15f);
        }

        sheet.getRow(32).getCell(4).setCellValue(certValid);
    }

    private void orderS502(XSSFSheet sheet, Record record, Language language) {

        sheet.getRow(15).getCell(6).setCellValue(record.getApplicant().getFullName());

        Address correspondenceAddress = record.getApplicant().getAddresses().stream()
                .filter(a -> Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE.equals(a.getAddressType().getCode())
                        && ServiceType.S502.equals(a.getServiceType()))
                .findFirst().orElse(null);
        String address = DOTS;
        if (correspondenceAddress != null) {
            address = StringUtils.hasText(correspondenceAddress.getFullAddress()) ? correspondenceAddress.getFullAddress() : DOTS;
        }

        sheet.getRow(18).getCell(5).setCellValue(address);

        sheet.getRow(20).getCell(4).setCellValue(record.getApplicant().getIdentifier());

        ApplicationS502 application = record.getApplicationS502();
        List<AddressBO> testingAddresses = application.getTestingAddresses();
        StringBuilder sbAddresses = new StringBuilder();
        for (AddressBO testingAddress : testingAddresses) {
            sbAddresses.append(testingAddress.getFullAddress()).append("\n");
        }
        if (testingAddresses.size() > 1) {
            sheet.getRow(23).setHeightInPoints(testingAddresses.size() * 25f);
        }

        sheet.getRow(23).getCell(4).setCellValue(sbAddresses.toString().trim());

        List<Nomenclature> cults = application.getPlantGroupTypes();
        StringBuilder sbCults = new StringBuilder();
        for (Nomenclature cult : cults) {
            sbCults.append(cult.getI18n(language).getName()).append("\n");
        }
        sheet.getRow(26).setHeightInPoints(testingAddresses.size() * 50f);
        sheet.getRow(26).getCell(4).setCellValue(sbCults.toString().trim());

        String applicantData = record.getApplicant().getFullName() + ", " + record.getApplicant().getIdentifier();
        sheet.getRow(29).getCell(2).setCellValue(applicantData);
    }

    private void applicationS503(XSSFSheet sheet, Record record, Language language) {
        sheet.getRow(13).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getContractorPaper().getRegNumber());
        sheet.getRow(13).getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getContractorPaper().getRegDate());
        sheet.getRow(15).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(record.getEntryNumber());
        sheet.getRow(15).getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(record.getEntryDate());
        sheet.getRow(22).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());

        sheet.getRow(25).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(DOTS);

        Address headOfficeAddress = record.getApplicant().getAddresses().stream()
                .filter(a -> Constants.ADDRESS_TYPE_HEAD_OFFICE_CODE.equals(a.getAddressType().getCode()))
                .findFirst().orElse(null);
        String address = DOTS;
        if (headOfficeAddress != null) {
            address = StringUtils.hasText(headOfficeAddress.getFullAddress()) ? headOfficeAddress.getFullAddress() : DOTS;
        }
        sheet.getRow(27).getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(address);
        sheet.getRow(29).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        sheet.getRow(32).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(StringUtils.hasText(record.getApplicationS503().getFullAddress())
                        ? record.getApplicationS503().getFullAddress()
                        : DOTS);

        String activityMan = DOTS;
        if (record.getApplicationS503().getCh83CertifiedPerson() != null) {
            activityMan = record.getApplicationS503().getCh83CertifiedPerson().getFullName();
        }
        sheet.getRow(36).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(activityMan);

        ContractorPaper paper = record.getContractorPaper();
        LocalDate paperDate = record.getEntryDate().plusYears(10L);
        if (paper != null) {
            paperDate = paper.getValidUntilDate() != null
                    ? paper.getValidUntilDate()
                    : paper.getRegDate().plusYears(10L);
        }
        sheet.getRow(39).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(paperDate);
        sheet.getRow(47).getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(DOTS);
    }

    private void applicationS1366(XSSFWorkbook wb, Record record, Language language) {
        XSSFSheet sheet1 = wb.getSheetAt(0);
        ApplicationS1366 application = record.getApplicationS1366();
        Facility facility = application.getFacility();
        if (facility != null) {
            sheet1.getRow(9).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(record.getApplicant().getFullName());
            sheet1.getRow(10).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(facility.getAddress().getFullAddress());
            sheet1.getRow(12).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(facility.getRegNumber());
        } else {
            sheet1.getRow(20).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(record.getApplicant().getFullName());
            record.getApplicant().getAddresses().stream()
                    .filter(a -> Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE.equals(a.getAddressType().getCode()))
                    .findFirst()
                    .ifPresent(address -> sheet1.getRow(21).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                            .setCellValue(address.getFullAddress()));
            // TODO: Add print of Registration number on row 24-23
        }

//        sheet1.getRow(13).getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
//                .setCellValue(""); // TODO: Регистрационен номер на обекта, когато се получи от е-формите

        sheet1.getRow(17).getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getBranch().getI18n(language).getName());

        sheet1.getRow(25).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(application.getRecipientName());

        sheet1.getRow(26).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(application.getRecipientCountry().getI18n(language).getName()
                        + ", " + application.getRecipientAddress());

        // TODO: Печатане на "Нето тегло", което е въведено от експерт
        sheet1.getRow(30).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue("Нето тегло, което е въведено от експерт");

        // TODO: Печатане на "Брой на опаковките", което е въведено от експерт
        sheet1.getRow(33).getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue("Брой на опаковките, което е въведено от експерт");

        // TODO: Печатане на "Държава на местоназначение", което е въведено от експерт
        sheet1.getRow(35).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue("Държава на местоназначение, която е въведено от експерт");

        // TODO: Печатане на "Температура на съхранение и транспортиране", което е въведено от експерт
        sheet1.getRow(37).getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue("Температура на съхранение и транспортиране, която е въведена от експерт");

        // TODO: Печатане на "Вид транспорт", което е въведено от експерт
        sheet1.getRow(40).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue("Вид транспорт, който е въведен от експерт");


        // --- Print sheet 2 ---

        XSSFSheet sheet2 = wb.getSheetAt(1);
        sheet2.getRow(2).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getContractorPaper().getRegNumber() + " / " + record.getContractorPaper().getRegDate());

        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        XSSFFont font = wb.createFont();
        font.setFamily(FontFamily.ROMAN);
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        int row = 0;
        style.setFont(font);

        List<ApplicationS1366Product> products = application.getApplicationS1366Products();
        for (int i = 0; i < products.size(); i++) {
            if (i >= 1) {
                row += 1;
                sheet2.createRow(6 + row);
                sheet2.getRow(6 + row).createCell(0).setCellStyle(style);
                sheet2.getRow(6 + row).createCell(1).setCellStyle(style);
                sheet2.getRow(6 + row).createCell(2).setCellStyle(style);
                sheet2.getRow(6 + row).createCell(3).setCellStyle(style);
                sheet2.getRow(6 + row).createCell(4).setCellStyle(style);
                sheet2.getRow(6 + row).createCell(5).setCellStyle(style);
                sheet2.getRow(6 + row).createCell(6).setCellStyle(style);
                sheet2.getRow(6 + row).createCell(7).setCellStyle(style);
                sheet2.getRow(6 + row).createCell(8).setCellStyle(style);
                sheet2.getRow(6 + row).createCell(9).setCellStyle(style);
                sheet2.getRow(6 + row).createCell(10).setCellStyle(style);
            }

            sheet2.getRow(6 + row).getCell(0).setCellValue((i + 1) + ".");
            ApplicationS1366Product product = products.get(i);
            if (product != null) {
                if (!CollectionUtils.isEmpty(product.getFoodTypes())) {
                    StringBuilder sb = new StringBuilder();
                    for (Classifier type : product.getFoodTypes()) {
                        sb.append("- ").append(type.getI18n(language).getName()).append("\n");
                    }
                    sheet2.getRow(6 + row).getCell(1).setCellValue(sb.toString().trim());
                    sheet2.getRow(6 + row).setHeightInPoints(product.getFoodTypes().size() * 15f);
                    sheet2.autoSizeColumn(1);
                }
                if (StringUtils.hasText(product.getProductName())) {
                    sheet2.getRow(6 + row).getCell(2).setCellValue(product.getProductName());
                    sheet2.autoSizeColumn(2);
                }
                if (StringUtils.hasText(product.getProductTrademark())) {
                    sheet2.getRow(6 + row).getCell(3).setCellValue(product.getProductTrademark());
                    sheet2.autoSizeColumn(3);
                }
                if (!CollectionUtils.isEmpty(product.getApplicationS1366ProductBatches())) {
                    StringBuilder sbUnitNetWeight = new StringBuilder();
                    StringBuilder sbNetWeight = new StringBuilder();
                    StringBuilder sbBatchNumbers = new StringBuilder();
                    for (ApplicationS1366ProductBatch batch : product.getApplicationS1366ProductBatches()) {
                        if (batch.getPerUnitNetWeight() != null) {
                            sbUnitNetWeight.append(batch.getPerUnitNetWeight())
                                    .append(" ")
                                    .append(batch.getPerUnitNetWeightUnit().getI18n(language).getName())
                                    .append("\n");
                        }
                        if (batch.getBatchNetWeight() != null) {
                            sbNetWeight
                                    .append(batch.getBatchNetWeight())
                                    .append(" ")
                                    .append(batch.getBatchNetWeightUnit().getI18n(language).getName())
                                    .append("\n");
                        }
                        if (StringUtils.hasText(batch.getBatchNumber())) {
                            sbBatchNumbers.append(batch.getBatchNumber()).append("\n");
                        }
                    }
                    if (StringUtils.hasText(sbUnitNetWeight)) {
                        sheet2.getRow(6 + row).getCell(4).setCellValue(sbUnitNetWeight.toString().trim());
                    }
                    if (StringUtils.hasText(sbNetWeight)) {
                        sheet2.getRow(6 + row).getCell(5).setCellValue(sbNetWeight.toString().trim());
                    }

                    float height = (float) product.getApplicationS1366ProductBatches().size();
                    if (!CollectionUtils.isEmpty(product.getFoodTypes())
                            && (product.getFoodTypes().size() > product.getApplicationS1366ProductBatches().size())) {
                        height = (float) product.getFoodTypes().size();
                    }
                    sheet2.getRow(6 + row).setHeightInPoints(height * 15f);

                    if (StringUtils.hasText(sbBatchNumbers)) {
                        sheet2.getRow(6 + row).getCell(7).setCellValue(sbBatchNumbers.toString().trim());
                        sheet2.autoSizeColumn(7);
                    }
                }
                if (product.getProductPackageType() != null) {
                    sheet2.getRow(6 + row).getCell(6).setCellValue(product.getProductPackageType());
                    sheet2.autoSizeColumn(6);
                }
                if (product.getProductManufactureDate() != null) {
                    sheet2.getRow(6 + row).getCell(8).setCellValue(product.getProductManufactureDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                }
                if (product.getProductExpiryDate() != null) {
                    sheet2.getRow(6 + row).getCell(9).setCellValue(product.getProductExpiryDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                }
                if (product.getProductCountry() != null) {
                    sheet2.getRow(6 + row).getCell(10).setCellValue(product.getProductCountry().getI18n(language).getName());
                }
            }
        }
    }

    private void applicationS2170(XSSFSheet sheet, Record record, Language language) {
        ContractorPaper paper = record.getContractorPaper();
        if (paper != null) {
            sheet.getRow(11).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(paper.getRegNumber() + " / " + paper.getRegDate());
        }

        sheet.getRow(16).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());

        sheet.getRow(19).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        ApplicationS2170 application = record.getApplicationS2170();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2170.class, "null");
        }

        sheet.getRow(21).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(application.getName());

        sheet.getRow(23).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(application.getManufacturer().getFullName()
                        + "; "
                        + application.getManufacturer().getIdentifier());

        if (application.getProductType() != null) {
            sheet.getRow(25).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(application.getProductType().getI18n(language).getName());
        }

        List<IngredientBO> ingredients = application.getIngredients();
        if (!CollectionUtils.isEmpty(ingredients)) {
            StringBuilder sbIngredients = new StringBuilder();
            for (IngredientBO ingredient : ingredients) {
                sbIngredients
                        .append("- ")
                        .append(ingredient.getName())
                        .append("; ")
                        .append(ingredient.getType())
                        .append("; ")
                        .append(ingredient.getAmount())
                        .append("\n");
            }
            sheet.getRow(28).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(sbIngredients.toString().trim());
            sheet.getRow(28).setHeightInPoints(ingredients.size() * 15f);
        }

        Set<CropBO> crops = application.getCrops();
        if (!CollectionUtils.isEmpty(crops)) {
            StringBuilder sbCrops = new StringBuilder();
            int counter = 0;
            for (CropBO crop : crops) {
                sbCrops.append("- ")
                        .append(crop.getCropName())
                        .append("\n");
                counter++;
                List<CropDoseBO> doses = crop.getCropDoses();
                if (!CollectionUtils.isEmpty(doses)) {
                    for (CropDoseBO dose : doses) {
                        sbCrops.append(" -- ").append(dose.getApplicationName()).append("\n");
                        counter++;
                    }
                }
                sheet.getRow(31).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(sbCrops.toString().trim());
                sheet.getRow(31).setHeightInPoints(counter * 15f);
            }
        }
    }

    private void applicationS2274(XSSFSheet sheet, Record record, Language language) {
        ContractorPaper paper = record.getContractorPaper();
        if (paper != null) {
            sheet.getRow(9).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(paper.getRegNumber());
            sheet.getRow(10).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(paper.getRegDate());
            if (paper.getValidUntilDate() != null) {
                sheet.getRow(45).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(paper.getValidUntilDate());
            }
        }

        ApplicationS2274 application = record.getApplicationS2274();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2274.class, "null");
        }

        sheet.getRow(14).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(application.getPppName());

        List<ApplicationS2274ActiveSubstanceDTO> activeSubstances = application.getActiveSubstances();
        if (!CollectionUtils.isEmpty(activeSubstances)) {
            StringBuilder sbActiveSubstances = new StringBuilder();
            for (int i = 1; i <= activeSubstances.size(); i++) {
                sbActiveSubstances
                        .append(i)
                        .append(". ")
                        .append(activeSubstances.get(i - 1).getActiveSubstanceName())
                        .append(" - ").append(activeSubstances.get(i - 1).getManufacturer())
                        .append(" / ").append(activeSubstances.get(i - 1).getManufacturerLat())
                        .append("\n\n");
            }
            sheet.getRow(17).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(sbActiveSubstances.toString().trim());
            sheet.getRow(17).setHeightInPoints(activeSubstances.size() * 45f);

        }

        Map<String, String> manufactures = application.getPppManufacturers();
        if (!CollectionUtils.isEmpty(manufactures)) {
            StringBuilder sbManufactures = new StringBuilder();
            int count = 0;
            for (Map.Entry<String, String> manufacture : manufactures.entrySet()) {
                sbManufactures.append(manufacture.getKey())
                        .append(" / ").append(manufacture.getValue())
                        .append("\n");
                count++;
            }

            sheet.getRow(20).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(sbManufactures.toString().trim());
            if (count > 1) {
                sheet.getRow(20).setHeightInPoints(count * 15f);
            }
        }

        Set<Nomenclature> functions = application.getPlantProtectionProductFunctions();
        if (!CollectionUtils.isEmpty(functions)) {
            StringBuilder sbFunctions = new StringBuilder();
            for (Nomenclature function : functions) {
                sbFunctions.append("- ")
                        .append(function.getI18n(language).getName())
                        .append(";")
                        .append("\n");
            }
            sheet.getRow(23).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(sbFunctions.toString().trim());
            sheet.getRow(23).setHeightInPoints(functions.size() * 15f);
        }

        Nomenclature formulationType = application.getFormulationType();
        if (formulationType != null) {
            sheet.getRow(25).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(formulationType.getI18n(language).getName());
        }

        Nomenclature category = application.getPlantProtectionProductCategory();
        if (category != null) {
            sheet.getRow(27).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(category.getI18n(language).getName());
        }

        List<ApplicationS2274PackageDTO> packages = application.getPackages();
        if (!CollectionUtils.isEmpty(packages)) {
            StringBuilder sbPackages = new StringBuilder();
            for (ApplicationS2274PackageDTO p : packages) {
                sbPackages.append(p.getSize())
                        .append(" / ")
                        .append(p.getMaterial())
                        .append("\n");
            }
            sheet.getRow(30).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(sbPackages.toString().trim());
            sheet.getRow(30).setHeightInPoints(packages.size() * 15f);
        }

        sheet.getRow(33).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(application.getPermitHolderName() + " / " + application.getPermitHolderNameLat());

        List<ApplicationS2274ActiveSubstanceDTO> substances = application.getActiveSubstances();
        if (!CollectionUtils.isEmpty(substances)) {
            StringBuilder sbCountries = new StringBuilder();
            for (ApplicationS2274ActiveSubstanceDTO substance : substances) {
                if (StringUtils.hasText(substance.getManufacturerCountryName())) {
                    sbCountries.append("- ").append(substance.getManufacturerCountryName()).append("\n");
                }
            }
            if (StringUtils.hasText(sbCountries)) {
                sheet.getRow(36).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(sbCountries.toString().trim());
                sheet.getRow(36).setHeightInPoints(substances.size() * 15f);
            }
        }

        List<ApplicationS2274ReferenceProductDTO> products = application.getReferenceProducts();
        if (!CollectionUtils.isEmpty(products)) {
            StringBuilder sbProducts = new StringBuilder();
            for (ApplicationS2274ReferenceProductDTO product : products) {
                if (StringUtils.hasText(product.getBgReferenceProduct())) {
                    sbProducts.append("- ")
                            .append(product.getBgReferenceProduct())
                            .append("\n");
                }
            }
            if (StringUtils.hasText(sbProducts)) {
                sheet.getRow(40).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(sbProducts.toString().trim());
                sheet.getRow(40).setHeightInPoints(substances.size() * 15f);
            }
        }
    }

    private void orderS2274(XSSFSheet sheet, Record record, Language language) {
        ApplicationS2274 application = record.getApplicationS2274();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2274.class, "null");
        }

        if (StringUtils.hasText(application.getOrderNumber())) {
            sheet.getRow(9).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(application.getOrderNumber());
            sheet.getRow(10).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(application.getOrderDate());
        }

        sheet.getRow(18).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(application.getPppName());

        List<ApplicationS2274ActiveSubstanceDTO> activeSubstances = application.getActiveSubstances();
        if (!CollectionUtils.isEmpty(activeSubstances)) {
            StringBuilder sbActiveSubstances = new StringBuilder();
            for (int i = 1; i <= activeSubstances.size(); i++) {
                sbActiveSubstances
                        .append(i)
                        .append(". ")
                        .append(activeSubstances.get(i - 1).getActiveSubstanceName())
                        .append(" - ").append(activeSubstances.get(i - 1).getManufacturer())
                        .append(" / ").append(activeSubstances.get(i - 1).getManufacturerLat())
                        .append("\n\n");
            }
            sheet.getRow(21).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(sbActiveSubstances.toString().trim());
            sheet.getRow(21).setHeightInPoints(activeSubstances.size() * 45f);
        }

        Map<String, String> manufactures = application.getPppManufacturers();
        if (!CollectionUtils.isEmpty(manufactures)) {
            StringBuilder sbManufactures = new StringBuilder();
            int count = 0;
            for (Map.Entry<String, String> manufacture : manufactures.entrySet()) {
                sbManufactures.append(manufacture.getKey())
                        .append(" / ").append(manufacture.getValue())
                        .append("\n");
                count++;
            }
            sheet.getRow(24).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(sbManufactures.toString().trim());
            if (count > 1) {
                sheet.getRow(24).setHeightInPoints(count * 15f);
            }
        }

        List<ApplicationS2274ActiveSubstanceDTO> substances = application.getActiveSubstances();
        if (!CollectionUtils.isEmpty(substances)) {
            StringBuilder sbCountries = new StringBuilder();
            for (ApplicationS2274ActiveSubstanceDTO substance : substances) {
                if (StringUtils.hasText(substance.getManufacturerCountryName())) {
                    sbCountries.append("- ").append(substance.getManufacturerCountryName()).append("\n");
                }
            }
            if (StringUtils.hasText(sbCountries)) {
                sheet.getRow(27).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(sbCountries.toString().trim());
                sheet.getRow(27).setHeightInPoints(substances.size() * 15f);
            }
        }

        List<ApplicationS2274ReferenceProductDTO> products = application.getReferenceProducts();
        if (!CollectionUtils.isEmpty(products)) {
            StringBuilder sbProducts = new StringBuilder();
            for (ApplicationS2274ReferenceProductDTO product : products) {
                if (StringUtils.hasText(product.getTradeName())) {
                    sbProducts.append("- ")
                            .append(product.getTradeName())
                            .append("\n");
                }
            }
            if (StringUtils.hasText(sbProducts)) {
                sheet.getRow(30).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(sbProducts.toString().trim());
                sheet.getRow(30).setHeightInPoints(substances.size() * 20f);
            }
        }

        Set<Nomenclature> functions = application.getPlantProtectionProductFunctions();
        if (!CollectionUtils.isEmpty(functions)) {
            StringBuilder sbFunctions = new StringBuilder();
            for (Nomenclature function : functions) {
                sbFunctions.append("- ")
                        .append(function.getI18n(language).getName())
                        .append(";")
                        .append("\n");
            }
            sheet.getRow(33).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(sbFunctions.toString().trim());
            sheet.getRow(33).setHeightInPoints(functions.size() * 15f);
        }

        Nomenclature formulationType = application.getFormulationType();
        if (formulationType != null) {
            sheet.getRow(35).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(formulationType.getI18n(language).getName());
        }

        Nomenclature category = application.getPlantProtectionProductCategory();
        if (category != null) {
            sheet.getRow(37).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(category.getI18n(language).getName());
        }

        List<ApplicationS2274PackageDTO> packages = application.getPackages();
        if (!CollectionUtils.isEmpty(packages)) {
            StringBuilder sbPackages = new StringBuilder();
            for (ApplicationS2274PackageDTO p : packages) {
                sbPackages.append(p.getSize())
                        .append(" / ")
                        .append(p.getMaterial())
                        .append("\n");
            }
            sheet.getRow(40).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(sbPackages.toString().trim());
            sheet.getRow(40).setHeightInPoints(packages.size() * 15f);
        }

        sheet.getRow(43).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(application.getPermitHolderName() + " / " + application.getPermitHolderNameLat());


        sheet.getRow(51).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(application.getPppName());

        sheet.getRow(53).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName() + "; " + record.getApplicant().getIdentifier());
    }

    private void applicationS3180(final Sheet sheet, final Record record, final Language language) {
        sheet.getRow(14).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getFacilityPaper().getRegNumber() + " / " + record.getFacilityPaper().getRegDate());

        String activityType = "";
        if (record.getFacility().getActivityType() != null) {
            activityType = record.getFacility().getActivityType().getI18n(language).getName();
        } else if (record.getFacility().getSubActivityType() != null) {
            activityType = record.getFacility().getSubActivityType().getI18n(language).getName();
        }
        sheet.getRow(20).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(StringUtils.hasText(activityType)
                        ? activityType : "?");
        sheet.getRow(23).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(StringUtils.hasText(record.getFacility().getAddress().getFullAddress())
                        ? record.getFacility().getAddress().getFullAddress()
                        : record.getFacility().getAddress().getAddress() + ", "
                        + record.getFacility().getAddress().getSettlement().getMunicipalityName());

        sheet.getRow(25).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());
        sheet.getRow(28).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        Address officeAddress = record.getApplicant()
                .getAddresses()
                .stream()
                .filter(a -> a.getAddressType().getCode().equals(Constants.ADDRESS_TYPE_HEAD_OFFICE_CODE))
                .findAny()
                .orElse(null);

        String officeAdr = DOTS;
        if (officeAddress != null) {
            officeAdr = officeAddress.getFullAddress();
        }
        sheet.getRow(29).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(officeAdr); // "Седалище на фирмата"

        Address correspondenceAddress = record.getApplicant()
                .getAddresses()
                .stream()
                .filter(a -> a.getAddressType().getCode().equals(Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE)
                        && ServiceType.S3180.equals(a.getServiceType()))
                .findAny()
                .orElse(null);

        String correspAdrd = DOTS;
        if (correspondenceAddress != null) {
            correspAdrd = correspondenceAddress.getFullAddress();
        }
        sheet.getRow(30).getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(correspAdrd);

        sheet.getRow(34).getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(record.getFacility().getRegNumber());
        String facilityGroupName = (record.getFacility() != null && record.getFacility().getActivityGroup() != null)
                ? record.getFacility().getActivityGroup().getI18n(language).getName()
                : "";
        sheet.getRow(37).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(facilityGroupName);

        Set<Nomenclature> relatedActivityCategories = record.getFacility().getRelatedActivityCategories();
        String racs = (!CollectionUtils.isEmpty(relatedActivityCategories)
                ? getActivities(relatedActivityCategories, language)
                : "");      // временно решение

        Set<Nomenclature> associatedActivityCategories = record.getFacility().getAssociatedActivityCategories();
        String aacs = (!CollectionUtils.isEmpty(associatedActivityCategories)
                ? getActivities(associatedActivityCategories, language)
                : "");   // временно решение

        sheet.getRow(38).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(racs);
        sheet.getRow(39).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(aacs);

        List<String> groups = (!CollectionUtils.isEmpty(record.getFacility().getFoodTypes())
                ? getClassifier(record.getFacility().getFoodTypes(), language)
                : null);
        if (groups != null) {
            StringBuilder sbGroups = new StringBuilder();
            for (int i = 1; i <= groups.size(); i++) {
                sbGroups.append(i).append(". ").append(groups.get(i - 1)).append("\n");
            }
            sheet.getRow(42).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(sbGroups.toString().trim());
            if (groups.size() > 1) {
                sheet.getRow(42).setHeightInPoints(groups.size() * 13f);
            }
        }

        String data = "";
        Address address = record.getApplicant()
                .getAddresses()
                .stream().filter(a -> Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE.equals(a.getAddressType().getCode())
                        && ServiceType.S3180.equals(a.getServiceType()))
                /*&& a.getRecord().getId().equals(record.getId())*/
                .findFirst()
                .orElse(null);

        if (address != null) {
            StringBuilder sb = new StringBuilder();

            if (StringUtils.hasText(address.getMail())) {
                sb.append(address.getMail()).append("; ");
            }
            if (StringUtils.hasText(address.getUrl())) {
                sb.append(address.getUrl()).append("; ");
            }
            if (StringUtils.hasText(address.getPhone())) {
                sb.append(address.getPhone()).append("; ");
            }
            if (StringUtils.hasText(address.getPostCode())) {
                sb.append(address.getPostCode()).append("; ");
            }
            if (StringUtils.hasText(address.getFullAddress())) {
                sb.append(address.getFullAddress()).append("; ");
            } else if (StringUtils.hasText(address.getAddress())) {
                sb.append(address.getAddress()).append("; ");
            }
            if (StringUtils.hasText(address.getAddressLat())) {
                sb.append(address.getAddressLat()).append("; ");
            }

            data = sb.toString();
        }

        sheet.getRow(47).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(data); // електронен адрес, интернет страница, телефонен номер, пощенски адрес, електронна поща  и други – когато е приложимо
        sheet.getRow(51).getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(DOTS); // "Име Фаминлия Презиме на Директор" - временно решение
        sheet.getRow(55).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getFacility().getAddress().getSettlement().getCode()); // "ЕКАТЕ на ОДБХ"
    }

    private void applicationS3201(XSSFSheet sheet, Record record, Language language) {

        sheet.getRow(12).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getContractorPaper().getRegNumber() + " / " + record.getContractorPaper().getRegDate());

        sheet.getRow(15).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());

        sheet.getRow(18).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());
    }

    private String getActivities(Set<Nomenclature> activities, Language language) {
        StringBuilder sb = new StringBuilder();
        for (Nomenclature activity : activities) {
            sb.append(activity.getI18n(language).getName());
            sb.append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    private List<String> getClassifier(Set<Classifier> classifiers, Language language) {
        List<String> classifiersList = new ArrayList<>();
        for (Classifier classifier : classifiers) {
            classifiersList.add(classifier.getI18n(language).getName());
        }
        return classifiersList;
    }

    private void applicationS2701(final Sheet sheet, Record record, Language language) {
        String branchName = record.getApplicant().getBranch().getI18n(language).getName();
        ContractorPaper contractorPaper = record.getContractorPaper();
        ApplicationS2701 application = record.getApplicationS2701();

        String trainingDocument = "";

        if (contractorPaper != null) {
            if (EducationalDocumentType.DIPLOMA.equals(application.getEducationalDocumentType())) {
                sheet.getRow(28).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("БЕЗСРОЧЕН");
                trainingDocument = "Диплома №";
            } else {
                sheet.getRow(28).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(contractorPaper.getRegDate().plusYears(10L));
                trainingDocument = "Сертификат";
            }
        }

        sheet.getRow(9).getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(branchName);
        sheet.getRow(13).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getContractorPaper().getRegNumber() + " / "
                        + record.getContractorPaper().getRegDate() + " г.");
        sheet.getRow(17).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(record.getApplicant().getFullName());
        sheet.getRow(21).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(trainingDocument);
        sheet.getRow(21).getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(application.getEducationalDocumentNumber()); // "T-T-T-T" временно решение
        sheet.getRow(23).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(application.getEducationalInstitution()); // "Име на висше учебно заведение"
        sheet.getRow(26).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(application.getEducationalInstitution()); // "професионално направление:"

        if (application.getCertificateImage() != null) {
            byte[] image = application.getCertificateImage();
            int PICTURE_TYPE;
            if (ImageValidator.isPng(image)) {
                PICTURE_TYPE = Workbook.PICTURE_TYPE_PNG;
            } else if (ImageValidator.isJpg(image)) {
                PICTURE_TYPE = Workbook.PICTURE_TYPE_JPEG;
            } else {
                throw new RuntimeException("The picture is not a PNG or JPEG image");
            }
            int imageID = sheet.getWorkbook().addPicture(image, PICTURE_TYPE);
            ClientAnchor imageAnchor = new XSSFClientAnchor(0, 0, 50, 50, 1, 10, 3, 15);
            //        imageAnchor.setCol1(1); // Sets the column (0 based) of the first cell.
            //        imageAnchor.setCol2(2); // Sets the column (0 based) of the Second cell.
            //        imageAnchor.setRow1(0); // Sets the row (0 based) of the first cell.
            //        imageAnchor.setRow2(1); // Sets the row (0 based) of the Second cell.

            XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
            drawing.createPicture(imageAnchor, imageID);
        }
    }

    private void refusalOrderS3180(final Sheet sheet, Record record, Language language) {
        String facilityName = "";
        String branchName = "";
        String facilityAddress = "";

        if (language.getLanguageId().equals("bg")) {
            branchName = record.getFacility().getBranch().getSettlement().getName();
            facilityName = record.getFacility().getI18n(language).getName();
            if (!StringUtils.hasText(facilityName)) {
                facilityName = record.getFacility().getActivityType().getI18n(language).getName();
            }
            if (StringUtils.hasText(record.getFacility().getAddress().getFullAddress())) {
                facilityAddress = record.getFacility().getAddress().getFullAddress();
            }
        } else {
            branchName = record.getFacility().getBranch().getSettlement().getNameLat();
            facilityAddress = record.getFacility().getAddress().getAddressLat();
        }

        String facilityManager = record.getApplicant().getFullName();
        String identifier = record.getApplicant().getIdentifier();

        Address managerAddress = record.getApplicant()
                .getAddresses()
                .stream()
                .filter(address -> address.getAddressType().equals(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE)))
                .findFirst()
                .get();

        sheet.getRow(16).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(branchName);
        sheet.getRow(18).getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(
                record.getEntryNumber() + " / " + record.getEntryDate());
        sheet.getRow(31).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(facilityName);
        sheet.getRow(34).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(facilityAddress);
        sheet.getRow(36).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(facilityManager);
        sheet.getRow(39).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(identifier);
        sheet.getRow(41).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(facilityAddress);
        sheet.getRow(43).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(managerAddress.getFullAddress());
        sheet.getRow(45).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(record.getApplicant().getEmail());
        sheet.getRow(55).getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(branchName);
    }

    private void refusalOrderS3125(XSSFSheet sheet, Record record, Language language) {
        sheet.getRow(12).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getEntryNumber());
        sheet.getRow(12).getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getEntryDate());

        sheet.getRow(18).getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(StringUtils.hasText(record.getIdentifier())
                        ? record.getIdentifier()
                        : "Record identifier");

        List<FoodSupplement> foodSupplements = record.getApplicationS3125().getFoodSupplements();
        StringBuilder sb = new StringBuilder();
        foodSupplements.forEach(fs -> {
            sb.append(fs.getI18n(language).getName()).append("; ");
        });
        sheet.createRow(28).createCell(2).setCellValue(sb.toString());

        sheet.getRow(31).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());

        sheet.getRow(34).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        Address address = record.getApplicant()
                .getAddresses()
                .stream()
                .filter(a -> a.getAddressType().equals(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE)))
                .findFirst()
                .get();
        sheet.getRow(36).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(address.getFullAddress());

        sheet.getRow(38).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getRequestor().getFullName() + "; " + record.getRequestor().getIdentifier());

        sheet.getRow(58).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getEntryNumber());
        sheet.getRow(58).getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getEntryDate());
    }

    private void applicationS2702(final Sheet sheet, final Record record, final Language language) {

        // --- 1. Supplier ---
        Contractor supplier = record.getApplicationS2702().getSupplier();
        sheet.getRow(14).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(getPersonData(supplier, record, language.getLanguageId()));

        // --- 3. Quarantine Station Person ---
        Contractor quarantineStationPerson = record.getApplicationS2702().getQuarantineStationPerson();
        sheet.getRow(16).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(getPersonData(quarantineStationPerson, record, language.getLanguageId()));

        // --- 4. Quarantine Station ---
        String stationSB = record.getApplicationS2702().getQuarantineStationName()
                + ", "
                + record.getApplicationS2702().getQuarantineStationAddress();
        sheet.getRow(17).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(stationSB);

        // --- 5. Material name ---
        sheet.getRow(18).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicationS2702().getMaterialName());

        // --- 6. Material quantity ---
        String weightPrefix = language.getLanguageId().equals(LANG_BG) ? WEIGHT_BG : WEIGHT_EN;
        Double materialTotalAmount = record.getApplicationS2702().getMaterialTotalAmount();
        String unit = record.getApplicationS2702().getMaterialMeasuringUnitCode().getI18n(language).getName();
        String materialData = weightPrefix + " - " + materialTotalAmount + " " + unit;
        sheet.getRow(19).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(materialData);

        // --- 7. Material type ---
        sheet.getRow(20).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicationS2702().getMaterialType());

        // --- 8. Material Packaging Condition ---
        sheet.getRow(21).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicationS2702().getMaterialPackagingCondition());

        // --- 9. Additional information ---
        sheet.getRow(22).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicationS2702().getMaterialPackagingCondition());

        // --- 10. Multiple send ---
        sheet.getRow(23).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(materialMovementsData(record.getApplicationS2702().getMaterialMovements(), language.getLanguageId()));

        // --- 11. Material end use ---
        sheet.getRow(24).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue((record.getApplicationS2702().getMaterialEndUse().getI18n(language).getName()));

        // --- 12. Endorsement ---
        String endorsement = "";
        String reference = "";  // ---  IMSOC reference ---
        if (LANG_BG.equals(language.getLanguageId())) {
            endorsement = ENDORSEMENT_TEXT_BG
                    + "\n\n"
                    + ENDORSEMENT_PLACE_BG
                    + "\n\n"
                    + ENDORSEMENT_DATE_BG
                    + "\n\n"
                    + ENDORSEMENT_SIGNATURE_BG;

            reference = IMSOC_BG;
        } else {
            endorsement = ENDORSEMENT_TEXT_EN
                    + "\n\n"
                    + ENDORSEMENT_PLACE_EN
                    + "\n\n"
                    + ENDORSEMENT_DATE_EN
                    + "\n\n"
                    + ENDORSEMENT_SIGNATURE_EN;

            reference = IMSOC_EN;
        }
        sheet.getRow(25).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(endorsement);

        // ---  IMSOC reference ---
        sheet.getRow(26).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(reference);
    }

    private void applicationS2711(XSSFSheet sheet, Record record, Language language) {
        sheet.getRow(13).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getContractorPaper().getRegNumber() + " / " + record.getContractorPaper().getRegDate());

        sheet.getRow(17).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(
                record.getEntryNumber() + " / " + record.getEntryDate());
        sheet.getRow(22).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());

        Address headOfficeAddress = record.getApplicant().getAddresses().stream()
                .filter(a -> Constants.ADDRESS_TYPE_HEAD_OFFICE_CODE.equals(a.getAddressType().getCode()))
                .findFirst().orElse(null);
        String address = DOTS;
        if (headOfficeAddress != null) {
            address = StringUtils.hasText(headOfficeAddress.getFullAddress()) ? headOfficeAddress.getFullAddress() : DOTS;
        }
        sheet.getRow(27).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(address);

        sheet.getRow(29).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        sheet.getRow(33).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicationS2711().getFacilityType().getI18n(language).getName());

        sheet.getRow(35).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(StringUtils.hasText(record.getApplicationS2711().getFacilityAddress().getFullAddress())
                        ? record.getApplicationS2711().getFacilityAddress().getFullAddress()
                        : DOTS);

        StringBuilder sbActivityMan = new StringBuilder();
        if (record.getApplicationS2711().getCh83CertifiedPerson() != null) {
            sbActivityMan.append(record.getApplicationS503().getCh83CertifiedPerson().getFullName())
                    .append("\n");
        }

        List<PersonBO> activityPersons = record.getApplicationS2711().getCh83CertifiedPersons();
        if (!CollectionUtils.isEmpty(activityPersons)) {
            activityPersons.forEach(ap -> sbActivityMan.append(ap.getFullName()).append("\n"));

            if (!activityPersons.isEmpty()) {
                sheet.getRow(38).setHeightInPoints((activityPersons.size() + 1f) * 15f);
            }
        }

        sheet.getRow(38).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(sbActivityMan.toString().trim());

        ContractorPaper paper = record.getContractorPaper();
        LocalDate paperDate = record.getEntryDate().plusYears(10L);
        if (paper != null) {
            paperDate = paper.getValidUntilDate() != null
                    ? paper.getValidUntilDate()
                    : paper.getRegDate().plusYears(10L);
        }
        sheet.getRow(41).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(paperDate + " г.");
    }

    private void applicationS2695(XSSFSheet sheet, Record record, Language language) {

        if (record.getContractorPaper() != null) {
            sheet.getRow(12).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(record.getContractorPaper().getRegNumber());
        }

        sheet.getRow(13).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getBranch().getI18n(language).getName() + ", " + LocalDate.now().getYear()); // TODO: may be to remove year?

        sheet.getRow(16).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getEntryNumber() + " / " + record.getEntryDate());

//        sheet.getRow(18).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
//                .setCellValue("Какво да се показва?");

        sheet.getRow(22).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());

        sheet.getRow(25).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        sheet.getRow(26).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getRequestor().getFullName() + ", " + record.getRequestor().getIdentifier());

        record.getApplicant().getAddresses().stream()
                .filter(a -> ServiceType.S2695.equals(a.getServiceType())
                        && Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE.equals(a.getAddressType().getCode()))
                .findFirst()
                .ifPresent(address -> sheet.getRow(27)
                        .getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(address.getFullAddress()));

        sheet.getRow(30).getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getBranch().getI18n(language).getName()); // TODO: Различно ли е от поддаденото в преписката

        ApplicationS2695 application = record.getApplicationS2695();
        sheet.getRow(33).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(application.getApplicationS2695Field().getTreatmentAddress().getFullAddress());


        List<Classifier> subAgricultures = application.getSubAgricultures();
        StringBuilder sbSubAgr = new StringBuilder();
        if (!CollectionUtils.isEmpty(subAgricultures)) {
            for (Classifier agr : subAgricultures) {
                sbSubAgr.append(" - ").append(agr.getI18n(language).getName()).append("\n");
            }
        }
        String culture = application.getAerialSprayAgriculturalGroup().getI18n(language).getName();
        String phenophase = application.getPhenophaseCulture().getI18n(language).getName();
        String culturePhenophae = "Култура: " + culture + "\n"
                + sbSubAgr
                + "Фенофаза на културата: \n" + "  " + phenophase;
        sheet.getRow(36).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(culturePhenophae);
        sheet.getRow(36).setHeightInPoints(40f + (subAgricultures.size() * 15f));

        sheet.getRow(39).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(application.getApplicationS2695Field().getTreatmentArea());

        List<ApplicationS2695Ppp> products = application.getPlantProtectionProducts();
        StringBuilder sbProducts = new StringBuilder();
        for (int i = 1; i <= products.size(); i++) {
            ApplicationS2695Ppp product = products.get(i - 1);
            sbProducts.append(i).append(". ").append(product.getPppName()).append("\n")
                    .append(product.getPppFunction().getI18n(language).getName());
        }
        sheet.getRow(42).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(sbProducts.toString().trim());
        sheet.getRow(42).setHeightInPoints(products.size() * 30f);


        String valid = application.getAerialSprayStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                + " - " + application.getAerialSprayEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        sheet.getRow(45).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(valid);
    }

    private void applicationS2697(XSSFSheet sheet, Record record, Language language) {
        sheet.getRow(9).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getEntryNumber() + " / " + record.getEntryDate() + " г.");

        String recordIdentifier = StringUtils.hasText(record.getIdentifier())
                ? record.getIdentifier() + " / " + record.getEntryDate() + " г. "
                : "номер от деловодната с-ма"; // TODO: Remove after get identifier

        sheet.getRow(11).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(recordIdentifier);

        ApplicationS2697 application = record.getApplicationS2697();
        List<SubstanceBO> substances = application.getSubstances();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= substances.size(); i++) {
            sb.append(i).append(". ").append(substances.get(i - 1).getName()).append("\n");
        }
        sheet.getRow(17).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(sb.toString());
        sheet.getRow(17).setHeightInPoints(substances.size() * 15f);

        sheet.getRow(23).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue("НА " + record.getApplicant().getFullName());

        sheet.getRow(26).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getRequestor().getFullName() + "; " + record.getRequestor().getIdentifier());

        Address headOfficeAddress = record.getApplicant()
                .getAddresses()
                .stream()
                .filter(address -> address.getAddressType().equals(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_HEAD_OFFICE_CODE)))
                .findFirst()
                .orElse(null);
        if (headOfficeAddress != null) {
            sheet.getRow(28).getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(headOfficeAddress.getFullAddress());
        }

        sheet.getRow(30).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        if (StringUtils.hasText(application.getWarehouseAddress().getFullAddress())) {
            sheet.getRow(33).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(application.getWarehouseAddress().getFullAddress());
        }

        record.getApplicant().getContractorPapers().stream()
                .filter(cp -> ServiceType.S2697.equals(cp.getServiceType()))
                .findFirst()
                .ifPresent(contractorPaper -> sheet.getRow(36)
                        .getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(contractorPaper.getValidUntilDate()));
    }

    private void orderS2698(XSSFSheet sheet, Record record, Language language) {
        String recordIdentifier = StringUtils.hasText(record.getIdentifier())
                ? record.getIdentifier()
                : "номер от деловодната с-ма"; // TODO: Remove after get identifier
        sheet.getRow(12).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(recordIdentifier);

        sheet.getRow(14).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());

        sheet.getRow(17).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        ApplicationS2698 application = record.getApplicationS2698();

        sheet.getRow(23).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(StringUtils.hasText(application.getAdjuvantName())
                        ? application.getAdjuvantName()
                        : application.getAdjuvantNameLat());

        List<AdjuvantBO> ingredients = application.getIngredients();
        StringBuilder sbIngredients = new StringBuilder();
        for (AdjuvantBO bo : ingredients) {
            String ingredient = "№ " + bo.getIngredientCasNumber() +
                    "; " + bo.getIngredientChemName() +
                    "; " + bo.getIngredientContent() +
                    " " + bo.getUnitTypeName();
            sbIngredients.append(ingredient).append("\n");
        }
        sheet.getRow(25).getCell(4).setCellValue(sbIngredients.toString().trim());
        if (ingredients.size() > 1) {
            sheet.getRow(25).setHeightInPoints(ingredients.size() * 15f);
        }

        sheet.getRow(28).getCell(5).setCellValue(application.getSupplier().getIdentifier());

        sheet.getRow(30).getCell(4).setCellValue(
                application.getManufacturerName() + ", " + application.getManufacturerIdentifier());

        sheet.getRow(32).getCell(4).setCellValue(
                application.getAdjuvantProductFormulationType().getI18n(language).getName()
                        + " " + application.getManufacturerIdentifier());

        sheet.getRow(34).getCell(4).setCellValue(application.getEffects());

        List<AdjuvantBO> applications = application.getApplications();
        StringBuilder sbCulture = new StringBuilder();
        for (AdjuvantBO adjuvantBO : applications) {
            sbCulture.append(adjuvantBO.getApplicationGrainCultureName())
                    .append("; Доза на приложение: ")
                    .append(adjuvantBO.getApplicationDose())
                    .append(" ")
                    .append(adjuvantBO.getUnitTypeName())
                    .append("\n");
        }
        sheet.getRow(36).getCell(3).setCellValue(sbCulture.toString().trim());
        if (applications.size() > 1) {
            sheet.getRow(36).setHeightInPoints(applications.size() * 15f);
        }

        List<Nomenclature> functions = application.getPppFunctions();
        StringBuilder sbFunctions = new StringBuilder();
        for (Nomenclature function : functions) {
            sbFunctions.append(function.getI18n(language).getName()).append("\n");
        }
        sheet.getRow(39).getCell(4).setCellValue(sbFunctions.toString().trim());
        if (functions.size() > 1) {
            sheet.getRow(39).setHeightInPoints(functions.size() * 15f);
        }
    }

    private void applicationS2869(final Sheet sheet, final Record record, final Language language) {
        sheet.getRow(14).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getEntryNumber() + " / " + record.getEntryDate());
        sheet.getRow(17).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(StringUtils.hasText(record.getIdentifier())
                        ? record.getIdentifier()
                        : "Record identifier");

        sheet.getRow(22).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());

        record.getApplicant()
                .getAddresses()
                .stream()
                .filter(a -> a.getAddressType().getCode().equals(Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE))
                .findAny()
                .ifPresent(officeAddress -> sheet.getRow(25)
                        .getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(StringUtils.hasText(officeAddress.getAddress())
                                ? officeAddress.getAddress()
                                : DOTS));

        sheet.getRow(27).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        List<FacilityDTO> facilities = record.getApplicationS2869().getFacilities();
        StringBuilder sbFacilities = new StringBuilder();
        int activityCounter = 0;
        for (int i = 0; i < facilities.size(); i++) {
            AddressDTO addressDTO = facilities.get(i).getAddress();
            String address = StringUtils.hasText(addressDTO.getFullAddress())
                    ? addressDTO.getFullAddress()
                    : (StringUtils.hasText(addressDTO.getSettlementName() + addressDTO.getAddress())
                    ? addressDTO.getSettlementName() + addressDTO.getAddress()
                    : ((StringUtils.hasText(addressDTO.getAddressLat())
                    ? addressDTO.getAddressLat()
                    : "")));

            String facilityNumber = facilities.get(i).getRegNumber();
            String facilityNameOrActivity = StringUtils.hasText(facilities.get(i).getName())
                    ? facilities.get(i).getName()
                    : (StringUtils.hasText(facilities.get(i).getActivityTypeName())
                    ? facilities.get(i).getActivityTypeName()
                    : "");
            sbFacilities.append(facilityNumber);
            sbFacilities.append(StringUtils.hasText(facilityNameOrActivity) ? (", " + facilityNameOrActivity) : "");
            sbFacilities.append(StringUtils.hasText(address) ? (", адрес: " + address) : "").append("; ");

            List<KeyValueDTO> activities = facilities.get(i).getActivityTypes();
            if (!CollectionUtils.isEmpty(activities)) {
                sbFacilities.append("\n").append("  Дейности:").append("\n");
                activities.forEach(a -> {
                    sbFacilities.append("    - ").append(a.getName()).append("; ").append("\n");
                });
                activityCounter += activities.size();
            }
            sbFacilities.append("\n");
        }
        sheet.getRow(30).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(sbFacilities.toString().trim());
        if (activityCounter > 0) {
            sheet.getRow(30).setHeightInPoints(activityCounter * 30f);
        }

    }

    private void orderS1590S2699S2700(XSSFSheet sheet, Record record, Language language) {
        sheet.getRow(12).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getEntryNumber() + " / " + record.getEntryDate() + " г.");

        String recordIdentifier = StringUtils.hasText(record.getIdentifier())
                ? record.getIdentifier()
                : "номер от деловодната с-ма"; // TODO: Remove after get identifier

        sheet.getRow(18).getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(recordIdentifier);
        sheet.getRow(18).getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getBranch().getI18n(language).getName());

        sheet.getRow(21).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());

        sheet.getRow(27).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        sheet.getRow(28).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getRequestor().getFullName() + ", " + record.getRequestor().getIdentifier());
    }

    private void orderS7691S7692S7693S7695(final Sheet sheet, Record record, ServiceType serviceType, Language language) {
        String identifier = StringUtils.hasText(record.getApplicant().getIdentifier())
                ? record.getApplicant().getIdentifier()
                : "Номер от деловодната система";
        sheet.getRow(11).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(identifier);
        sheet.getRow(15).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getBranch().getI18n(language).getName());
        sheet.getRow(18).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getEntryNumber() + " / " + record.getEntryDate() + " г.");

        Facility facility = null;
        switch (serviceType) {
            case S7691:
                facility = record.getApplicationS7691().getFacility();
                break;
            case S7692:
                facility = record.getApplicationS7692().getFacility();
                break;
            case S7693:
                facility = record.getApplicationS7693().getFacility();
                break;
            case S7695:
                facility = record.getApplicationS7695().getFacility();
                break;
            default:
                break;
        }

        if (facility != null) {
            String facilityName = "";
            if (StringUtils.hasText(facility.getI18n(language).getName())) {
                facilityName = facility.getI18n(language).getName()
                        + "\n"
                        + facility.getActivityType().getI18n(language).getName();
                sheet.getRow(32).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(facilityName);
                sheet.getRow(32).setHeightInPoints(40f);
            } else {
                facilityName = facility.getActivityType().getI18n(language).getName();
                sheet.getRow(32).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(facilityName);
            }
            if (StringUtils.hasText(facility.getAddress().getFullAddress())) {
                sheet.getRow(35).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(facility.getAddress().getFullAddress());
            }
        }

        sheet.getRow(37).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());

        sheet.getRow(40).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        record.getApplicant()
                .getAddresses()
                .stream()
                .filter(address -> address.getAddressType().equals(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_HEAD_OFFICE_CODE)))
                .findFirst()
                .ifPresent(headOffice -> sheet.getRow(42).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(headOffice.getFullAddress()));

        record.getApplicant()
                .getAddresses()
                .stream()
                .filter(address -> address.getAddressType().equals(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE)))
                .findFirst().ifPresent(headOffice -> sheet.getRow(44).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(headOffice.getFullAddress()));

        sheet.getRow(46).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getEmail());
    }

    private void orderS7694(final Sheet sheet, Record record, Language language) {
        String identifier = StringUtils.hasText(record.getApplicant().getIdentifier())
                ? record.getApplicant().getIdentifier()
                : "Номер от деловодната система";
        sheet.getRow(11).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(identifier);
        sheet.getRow(15).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getBranch().getI18n(language).getName());
        sheet.getRow(18).getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getEntryNumber() + " / " + record.getEntryDate() + " г.");

        String facilityName = record.getApplicationS7694().getName();
//        if (!StringUtils.hasText(facilityName)) {
//            facilityName = record.getFacility().getActivityType().getI18n(language).getName();
//        }
        sheet.getRow(32).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(facilityName);

        if (record.getFacility() != null && StringUtils.hasText(record.getFacility().getAddress().getFullAddress())) {
            sheet.getRow(35).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(record.getFacility().getAddress().getFullAddress());
        }

        sheet.getRow(37).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getFullName());

        sheet.getRow(40).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getIdentifier());

        record.getApplicant()
                .getAddresses()
                .stream()
                .filter(address -> address.getAddressType().equals(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_HEAD_OFFICE_CODE)))
                .findFirst()
                .ifPresent(headOffice -> sheet.getRow(42).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(headOffice.getFullAddress()));

        record.getApplicant()
                .getAddresses()
                .stream()
                .filter(address -> address.getAddressType().equals(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE)))
                .findFirst().ifPresent(headOffice -> sheet.getRow(44).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(headOffice.getFullAddress()));

        sheet.getRow(46).getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .setCellValue(record.getApplicant().getEmail());
    }

    private String getPersonData(final Contractor person, final Record record, final String languageId) {
        StringBuilder sb = new StringBuilder();

        sb.append(StringUtils.hasText(person.getDegree())
                ? person.getDegree() + " " + person.getFullName()
                : person.getFullName());

        Address address = record.getApplicationS2702().getSupplierAddress();
        if (address != null) {
            if (languageId.equals(LANG_BG)) {
                sb.append("\n").append(address.getAddress());
            } else {
                sb.append("\n").append(address.getAddressLat());
            }
        }

        String phonePrefix = languageId.equals(LANG_BG) ? PHONE_BG : PHONE_EN;
        String emailPrefix = languageId.equals(LANG_BG) ? EMAIL_BG : EMAIL_EN;
        sb.append(StringUtils.hasText(person.getEmail())
                ? "\n" + emailPrefix + person.getEmail()
                : "");
        sb.append(StringUtils.hasText(person.getPhone())
                ? "\n" + phonePrefix + person.getPhone()
                : "");

        return sb.toString();
    }

    private String materialMovementsData(final List<Double> materialMovements, final String languageId) {
        StringBuilder sb = new StringBuilder();

        if (!CollectionUtils.isEmpty(materialMovements)) {
            materialMovements.forEach(sb::append);
        } else {
            sb.append(languageId.equals(LANG_BG)
                    ? MULTIPLE_SEND_TEXT_BG + "\n" + MULTIPLE_SEND_TYPE_TEXT_BG
                    : MULTIPLE_SEND_TEXT_EN + "\n" + MULTIPLE_SEND_TYPE_TEXT_EN);
        }

        return sb.toString();
    }

    private XSSFCellStyle getCellStyle(XSSFSheet sheet) {
        XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
        XSSFFont font = sheet.getWorkbook().createFont();
        font.setFontHeight(12);
        font.setFamily(FontFamily.ROMAN);
        font.setFontName("Times New Roman");
        style.setFont(font);
        return style;
    }
}
