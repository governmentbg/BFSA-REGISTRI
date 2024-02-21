import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ContractorCertificates83TableComponent } from './contractor-certificates83-table.component';

describe('ContractorCertificates83TableComponent', () => {
  let component: ContractorCertificates83TableComponent;
  let fixture: ComponentFixture<ContractorCertificates83TableComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [NoopAnimationsModule, MatSnackBarModule, MatDialogModule],
    });
    fixture = TestBed.createComponent(ContractorCertificates83TableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
