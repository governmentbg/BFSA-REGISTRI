import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';

import { EntranceTypeComponent } from './entrance-type.component';

describe('EntranceTypeComponent', () => {
  let component: EntranceTypeComponent;
  let fixture: ComponentFixture<EntranceTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EntranceTypeComponent],
      imports: [MatCardModule],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EntranceTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
