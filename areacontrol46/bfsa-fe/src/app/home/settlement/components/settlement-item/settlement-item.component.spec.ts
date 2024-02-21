import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';

import { SettlementItemComponent } from './settlement-item.component';

describe('SettlementItemComponent', () => {
  let component: SettlementItemComponent;
  let fixture: ComponentFixture<SettlementItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SettlementItemComponent],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        MatSnackBarModule,
        MatDialogModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SettlementItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
