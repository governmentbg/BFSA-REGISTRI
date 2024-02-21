import { HttpClientModule } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';

import { VerifyPasswordComponent } from './verify-password.component';

describe('VerifyPasswordComponent', () => {
  let component: VerifyPasswordComponent;
  let fixture: ComponentFixture<VerifyPasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerifyPasswordComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [
        ReactiveFormsModule,
        HttpClientModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatIconModule,
        MatCardModule,
        BrowserAnimationsModule,
        RouterModule.forRoot([]),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerifyPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
