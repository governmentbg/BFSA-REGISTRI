import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ContractorVehiclesDialogComponent } from './contractor-vehicles-dialog.component';

describe('ContractorVehiclesDialogComponent', () => {
  let component: ContractorVehiclesDialogComponent;
  let fixture: ComponentFixture<ContractorVehiclesDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContractorVehiclesDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [NoopAnimationsModule, HttpClientModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ContractorVehiclesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
