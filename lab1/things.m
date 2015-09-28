

%pathFile = fopen('path.txt', 'r');
path = csvread('path.txt');
%fclose(pathFile);

%cpsFile = fopen('cps.txt', 'r');
cps = csvread('cps.txt');
%fclose(cpsFile);

robotPos = csvread('robotPos.txt');

plot(path(:, 1), path(:, 2), 'b', cps(:, 1), cps(:, 2), 'r', robotPos(:, 1), robotPos(:, 2), 'g'); 