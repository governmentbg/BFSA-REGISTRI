import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';

import { NomenclaturesComponent } from './nomenclatures.component';

describe('NomenclaturesComponent', () => {
  let component: NomenclaturesComponent;
  let fixture: ComponentFixture<NomenclaturesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NomenclaturesComponent],
      imports: [HttpClientModule, MatDialogModule, MatTableModule],
    }).compileComponents();

    fixture = TestBed.createComponent(NomenclaturesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
