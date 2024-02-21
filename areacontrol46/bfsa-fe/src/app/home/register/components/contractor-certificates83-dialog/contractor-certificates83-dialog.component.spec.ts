import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ContractorCertificates83DialogComponent } from './contractor-certificates83-dialog.component';

describe('ContractorCertificates83DialogComponent', () => {
  let component: ContractorCertificates83DialogComponent;
  let fixture: ComponentFixture<ContractorCertificates83DialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContractorCertificates83DialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [NoopAnimationsModule],
    });
    fixture = TestBed.createComponent(ContractorCertificates83DialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
