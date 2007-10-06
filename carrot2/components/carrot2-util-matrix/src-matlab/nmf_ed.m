%
% Non-negative Matrix Factorisation
% Euclidean Distance Algorithm
%
% A - input matrix
% U - base vectors matrix
% V - coefficient matrix
% C - approximation quality for subsequent iterations
%
function [U, V, C] = nmf_ed(A)
    [m, n]  = size(A);               
    k       = 2;			% the desired number of base vectors
    maxiter = 50;           % the number of iterations
    eps     = 1e-9;         % machine epsilon
    
    U = rand(m, k);         % initialise U randomly
    V = rand(n, k);         % initialise V randomly
    
    for iter = 1:maxiter
        V = V.*((A'*U+eps)./(V*(U'*U)+eps));    % update V
        U = U.*((A*V+eps)./(U*(V'*V)+eps));		% update U
        C(1, iter) = norm((A-U*V'), 'fro');		% approximation quality
    end
    
