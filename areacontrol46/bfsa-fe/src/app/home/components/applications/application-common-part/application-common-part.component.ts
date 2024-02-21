import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { BranchInterface } from 'src/app/home/branch/interfaces/branch-interface';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';

@Component({
  selector: 'app-application-common-part',
  templateUrl: './application-common-part.component.html',
  styleUrls: ['./application-common-part.component.scss'],
})
export class ApplicationCommonPartComponent implements OnInit {
  @Input()
  public commonPartData: any;
  public requestorAuthorTypes: NomenclatureInterface[];
  public branches: BranchInterface[];
  public commonPartForm: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService
  ) {}

  ngOnInit() {
    this.getAllRequestorAuthorTypes();
    this.getAllBranches();
    if (this.commonPartData) {
      this.commonPartForm = this.fb.group({
        entityType: [this.commonPartData.entityType],
        recordId: [this.commonPartData.recordId],
        branchIdentifier: [this.commonPartData.branchIdentifier],
        requestorFullName: [this.commonPartData.requestorFullName],
        requestorIdentifier: [this.commonPartData.requestorIdentifier],
        requestorEmail: [this.commonPartData.requestorEmail],
        requestorPhone: [this.commonPartData.requestorPhone],
        requestorAuthorTypeCode: [this.commonPartData.requestorAuthorTypeCode],
        requestorPowerAttorneyNumber: [
          [this.commonPartData?.requestorPowerAttorneyNumber],
        ],
        requestorPowerAttorneyNotary: [
          [this.commonPartData?.requestorPowerAttorneyNotary],
        ],
        requestorPowerAttorneyDate: [
          [this.commonPartData?.requestorPowerAttorneyDate],
        ],
        requestorPowerAttorneyUntilDate: [
          [this.commonPartData?.requestorPowerAttorneyUntilDate],
        ],
        applicantFullName: [this.commonPartData.applicantFullName],
        applicantIdentifier: [this.commonPartData.applicantIdentifier],
        applicantEmail: [this.commonPartData.applicantEmail],
        applicantPostCode: [this.commonPartData.applicantPostCode],
        applicantAddress: [this.commonPartData.applicantAddress],
        applicantPhone: [this.commonPartData.applicantPhone],
        applicantCorrespondenceAddress: this.fb.group({
          address: [
            this.commonPartData.applicantCorrespondenceAddress?.address,
          ],
          addressTypeCode: [
            this.commonPartData.applicantCorrespondenceAddress?.addressTypeCode,
          ],
          settlementCode: [
            this.commonPartData.applicantCorrespondenceAddress?.settlementCode,
          ],
          postCode: [
            this.commonPartData.applicantCorrespondenceAddress?.postCode,
          ],
          fullAddress: [
            this.commonPartData.applicantCorrespondenceAddress?.fullAddress,
          ],
        }),
      });
    }

    this.commonPartForm?.disable();
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
  }

  private getAllBranches() {
    this.branchService.getBranches().subscribe((response) => {
      this.branches = response.results;
    });
  }

  public get isPhysicalPersonForm() {
    return this.commonPartForm?.get('entityType')?.value === 'PHYSICAL';
  }
}
