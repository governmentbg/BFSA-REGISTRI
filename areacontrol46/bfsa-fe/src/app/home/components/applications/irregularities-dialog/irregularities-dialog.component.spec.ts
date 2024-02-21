import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IrregularitiesDialogComponent } from './irregularities-dialog.component';

describe('IrregularitiesDialogComponent', () => {
  let component: IrregularitiesDialogComponent;
  let fixture: ComponentFixture<IrregularitiesDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IrregularitiesDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IrregularitiesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
