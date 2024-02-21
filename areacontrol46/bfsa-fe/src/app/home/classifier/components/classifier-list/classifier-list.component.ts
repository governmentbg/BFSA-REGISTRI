import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClassifierInterface } from '../../interfaces/classifier-interface';
import { ClassifierService } from '../../services/classifier.service';
import { ClassifierDialogComponent } from '../classifier-dialog/classifier-dialog.component';

@Component({
  selector: 'app-classifier-list',
  templateUrl: './classifier-list.component.html',
  styleUrls: ['./classifier-list.component.scss'],
})
export class ClassifierListComponent implements OnInit {
  classifiers: ClassifierInterface[];

  constructor(
    public dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private classifiersService: ClassifierService
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.classifiersService
      .getClassifiers()
      .subscribe((classifiers: ClassifierInterface[]) => {
        this.classifiers = classifiers;
      });
  }

  addClassifier() {
    const dialogRef = this.dialog.open(ClassifierDialogComponent, {
      width: '400px',
      data: { isAdd: true },
    });
    dialogRef.afterClosed().subscribe((classifier: ClassifierInterface) => {
      if (classifier) {
        console.log(classifier);
        this.classifiersService
          .addClassifier(classifier)
          .subscribe({
            next: () => {
              this.initialize();
              this._snackBar.open('Classifier added successfully', '', {
                duration: 1000,
              });
            },
            error: (err) => {
              this._snackBar.open(
                `Error while adding the classifier: ${err.message}`,
                'Close'
              );
            },
          });
      }
    });
  }
}
