import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { SettlementListComponent } from './settlement-list.component';

describe('SettlementListComponent', () => {
  let component: SettlementListComponent;
  let fixture: ComponentFixture<SettlementListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SettlementListComponent],
      imports: [
        HttpClientModule,
        MatDialogModule,
        MatIconModule,
        MatSnackBarModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SettlementListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
