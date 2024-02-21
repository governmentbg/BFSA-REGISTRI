import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantSalesmanTableComponent } from './tenant-salesman-table.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('TenantSalesmanTableComponent', () => {
  let component: TenantSalesmanTableComponent;
  let fixture: ComponentFixture<TenantSalesmanTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TenantSalesmanTableComponent],
      imports: [NoopAnimationsModule, MatSnackBarModule, MatDialogModule],
    }).compileComponents();

    fixture = TestBed.createComponent(TenantSalesmanTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
