import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractorSeedsTableComponent } from './contractor-seeds-table.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ContractorSeedsTableComponent', () => {
  let component: ContractorSeedsTableComponent;
  let fixture: ComponentFixture<ContractorSeedsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContractorSeedsTableComponent ],
      imports: [NoopAnimationsModule, MatSnackBarModule, MatDialogModule],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContractorSeedsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
