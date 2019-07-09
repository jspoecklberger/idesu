# Simple release planning

A project that aims to simplify release planning based on constraint satisfaction.

This library consists of an interface to a release plan model, a constraint solving model, and a toolkit for conflict and diagnosis handling. The interface is used as a contract for your model based on releases, requirements and constraints.
The model is then solved using a constraint solving approach based on choco library. <link>
It can be used in two ways. The first usecase is to check for model consistency. Second, it can be used to generate or complete release plans.

## Getting Started

Download the sources and add it to your project


### Usage

Here is an example on how to use this library. In this case, we would like to check a model for consistency:

First, we define our model by instanciating an instance of SimpleReleasePlan which implements IReleaseplan.  

```
 List<Requirement> requirements = new ArrayList<Requirement>(
                Arrays.asList(
                        new Requirement(1, 2, 3),
                        new Requirement(2, 2, 3),
                        new Requirement(3, 2, 3),
                        new Requirement(4, 2, 3)));

        List<Release> releases = new ArrayList<Release>(
                Arrays.asList(
                        new Release(10, Arrays.asList(1, 4), 3),
                        new Release(11, Arrays.asList(2, 3), 3)));

        SimpleReleasePlan plan = new SimpleReleasePlan(requirements, releases);
```

Next, we create some constraints that we want to add. 
Note that previously defined release capacities do not implicitly create constraints

```
 List<Constraint> constraints = ConstraintHelper.createCapacityConstraints(plan);
```

Now we instanciate our consistency checker model, build it and check for consistency.

```
ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(plan, constraints);
m.build();
m.checkConsistency() //false
```

We can get a diagnosis by calling m.getDiagnosedConstraints() which implicitly makes
use of the FastDiag-Algorithm.

```
List<Constraint> diagnosis = m.getDiagnosedConstraints();
Assert.assertTrue(diagnosis.size() == 1);
Assert.assertTrue(diagnosis.get(0).getConstraintId() == 10);
```
Finally, we remove the diagnosed constraints and check for consistency again.

```
constraints.removeIf(x -> m.getDiagnosedConstraints().stream().anyMatch(x::equals));
m.build();
Assert.assertTrue(m.checkConsistency()); //true
```

Further usage examples can be looked up in the tests!

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Choco](http://www.choco-solver.org/) - An Open-Source java library for constraint programming


## License

This project is licensed under the MIT License

