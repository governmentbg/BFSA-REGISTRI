import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';

import { SubNomenclaturesComponent } from './sub-nomenclatures.component';

describe('SubNomenclaturesComponent', () => {
  let component: SubNomenclaturesComponent;
  let fixture: ComponentFixture<SubNomenclaturesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubNomenclaturesComponent],
      imports: [MatDialogModule, HttpClientModule, MatTableModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubNomenclaturesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
