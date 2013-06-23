function error_test = ...
    learningCurveTest(X, y, Xtest, ytest, lambda)
%LEARNINGCURVE Generates the train and cross validation set errors needed 
%to plot a learning curve
%   [error_train, error_val] = ...
%       LEARNINGCURVE(X, y, Xval, yval, lambda) returns the train and
%       cross validation set errors for a learning curve. In particular, 
%       it returns two vectors of the same length - error_train and 
%       error_val. Then, error_train(i) contains the training error for
%       i examples (and similarly for error_val(i)).
%
%   In this function, you will compute the train and test errors for
%   dataset sizes from 1 up to m. In practice, when working with larger
%   datasets, you might want to do this in larger intervals.
%


% You need to return these values correctly
error_test = 0;

           
[theta] = trainLinearReg(X, y, lambda);
error_test = linearRegCostFunction(Xtest, ytest, theta, 0);

end
