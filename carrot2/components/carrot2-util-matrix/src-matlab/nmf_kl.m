% updating of V: transposition moved inside the brackets

%
% Non-negative Matrix Factorisation
% Divergence Algorithm
%
% A - input matrix
% U - base vectors matrix
% V - coefficient matrix
% C - approximation quality for subsequent iterations
%
function [U, V, C] = nmf_kl(A)
    [m, n]  = size(A);               
    k       = 2;			% the desired number of base vectors
    maxiter = 50;           % the number of iterations
    eps     = 1e-9;         % machine epsilon
    
    U = rand(m, k);         % initialise U randomly
    V = rand(n, k);         % initialise V randomly
    O = ones(m, m);			% a matrix of ones
    
    for iter = 1:maxiter
        V = V.*(((A+eps)./(U*V'+eps))'*U);		% update V
        U = U.*(((A+eps)./(U*V'+eps))*V);		% update U
        U = U./(O*U);							% normalise U's columns
        C(1, iter) = norm((A-U*V'), 'fro');		% approximation quality
    end
    
