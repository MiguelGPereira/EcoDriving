package com.nau.lgp.feup;

import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LPSolution;
import scpsolver.problems.LPWizard;
import scpsolver.problems.LinearProgram;

import java.util.Arrays;
/*Min 5 x + 10 y
Subject to
3 x + 1 y >= 8
4y >= 4
2x <= 2*/
public class Main {

    public static void main(String[] args) {
            double total = 8*0.06;
            double accMax = 12960;
            double initialVelocity[] = new double[8]; // inicial = final[current-1] and 1º = 0
            double finalVelocity[] = new double[8]; //  initialVelocity + RealAcceleration*temp and last = 0
            int acceleration[] = new int[8]; // -1, 0, 1 variaveis de decisao
            double RealAcceleration[] = new double[8]; // accMax*acceleration
            double SqrtTime[] = new double[8];//vi^2 - 4*Acc/2 * (Pkinicial -PkFinal)
            double time[] = new double[8]; // if(RealAcc ==0) {t=dist/vel} else if(SqrtTime[current]>=0) {t=(-vi+SqrtTime[current])/(2*RAcc/2)} else t = 3600
            double PKfinal[] = new double[8];
            double PKinicial[] = new double[8];
            double distPercorrida[] = new double[8]; //Pkfinal - Pkfinal
            double factor[] = new double[8]; // factor[u] = factor[u-1]/2; 1º = 8192
            double positiveFactor[] = new double[8]; // factor * dist * (Vfinal-Vinicial)/2
            double Vmax = 50;

            //obj -> max SUM positveFactor
            //Sum array time  = max time

            double maxTime = 60.0;

           // for(double i=0; i<total;i+=0.06){
              //      double Vi = 0;
                //    double Vf = Vi+RealAcceleration*t;
                    LinearProgram lp = new LinearProgram(new double[]{5.0,10.0});
                    lp.addConstraint(new LinearBiggerThanEqualsConstraint(new double[]{3.0,1.0}, 8.0, "c1"));
                    lp.addConstraint(new LinearBiggerThanEqualsConstraint(new double[]{0.0,4.0}, 4.0, "c2"));
                    lp.addConstraint(new LinearSmallerThanEqualsConstraint(new double[]{2.0,0.0}, 2.0, "c3"));
                    lp.setMinProblem(true);
                    LinearProgramSolver solver  = SolverFactory.newDefault();
                    double[] sol = solver.solve(lp);
                    System.out.println(Arrays.toString(sol));
            //}



    }
}
