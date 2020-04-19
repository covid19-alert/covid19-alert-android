package com.immotef.dashboardrepository.data

import com.immotef.dashboardrepository.R


/**
 *
 */


sealed class InfectionState {
    abstract val colorFrom: Int
    abstract val colorTo: Int
    abstract val textOfRiskBeingInfected: Int
    abstract val riskTextFromColor: Int
    abstract val riskTextColor: Int
    abstract val riskLevel: Int
    abstract val infectedPeopleMet: Int
    abstract val allPeopleMet: Int
    abstract val circleColorFrom: Int
    abstract val circleColorTo: Int
    abstract val backgroundIcon: Int
    abstract val previousRotation: Int
    val rotation: Int get() = (riskLevel - 50) * 9 / 5
    open val reportInfectionButtonText = R.string.dashboard_report_infection
    open val shouldShowReportButton: Boolean = true
    open val shouldShowMultilineTitle = true
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InfectionState) return false

        if (textOfRiskBeingInfected != other.textOfRiskBeingInfected) return false
        if (riskLevel != other.riskLevel) return false
        if (infectedPeopleMet != other.infectedPeopleMet) return false
        if (allPeopleMet != other.allPeopleMet) return false

        return true
    }

    override fun hashCode(): Int {
        var result = textOfRiskBeingInfected
        result = 31 * result + riskLevel
        result = 31 * result + infectedPeopleMet
        result = 31 * result + allPeopleMet
        return result
    }


}


class LowRiskState(oldColor: Int,
                   oldCircleColor: Int,
                   riskColorFrom: Int,
                   totalPeople: Int,
                   infectedPeople: Int,
                   previousRotation: Int,
                   override val riskLevel: Int) : InfectionState() {
    override val colorFrom: Int = oldColor
    override val colorTo: Int = R.color.low_background
    override val textOfRiskBeingInfected: Int = R.string.low_risk
    override val riskTextFromColor: Int = riskColorFrom
    override val riskTextColor: Int = R.color.low_risk

    override val infectedPeopleMet: Int = infectedPeople
    override val allPeopleMet: Int = totalPeople
    override val circleColorFrom: Int = oldCircleColor
    override val circleColorTo: Int = R.color.low_risk
    override val backgroundIcon: Int = R.drawable.ic_low_risk_background
    override val previousRotation: Int = previousRotation
}

class MediumRiskState(oldColor: Int,
                      oldCircleColor: Int,
                      riskColorFrom: Int,
                      totalPeople: Int,
                      infectedPeople: Int,
                      previousRotation: Int,
                      override val riskLevel: Int) : InfectionState() {
    override val colorFrom: Int = oldColor
    override val colorTo: Int = R.color.medium_backgroun
    override val textOfRiskBeingInfected: Int = R.string.medium_risk
    override val riskTextFromColor: Int = riskColorFrom
    override val riskTextColor: Int = R.color.medium_risk


    override val infectedPeopleMet: Int = infectedPeople
    override val allPeopleMet: Int = totalPeople
    override val circleColorFrom: Int = oldCircleColor
    override val circleColorTo: Int = R.color.high_risk
    override val backgroundIcon: Int = R.drawable.ic_middle_risk_backgroun
    override val previousRotation: Int = previousRotation

}

class HighRiskState(oldColor: Int,
                    oldCircleColor: Int,
                    riskColorFrom: Int,
                    totalPeople: Int,
                    infectedPeople: Int,
                    previousRotation: Int,
                    override val riskLevel: Int) : InfectionState() {
    override val colorFrom: Int = oldColor
    override val colorTo: Int = R.color.risk_background
    override val textOfRiskBeingInfected: Int = R.string.high_risk
    override val riskTextFromColor: Int = riskColorFrom
    override val riskTextColor: Int = R.color.high_risk

    override val infectedPeopleMet: Int = infectedPeople
    override val allPeopleMet: Int = totalPeople
    override val circleColorFrom: Int = oldCircleColor
    override val circleColorTo: Int = R.color.high_risk
    override val backgroundIcon: Int = R.drawable.ic_high_risk_background
    override val previousRotation: Int = previousRotation


}

class InfectedState(oldColor: Int, oldCircleColor: Int, riskColorFrom: Int, totalPeople: Int, infectedPeople: Int, previousRotation: Int) : InfectionState() {
    override val colorFrom: Int = oldColor
    override val colorTo: Int = R.color.risk_background
    override val textOfRiskBeingInfected: Int = R.string.infected_state
    override val riskTextFromColor: Int = riskColorFrom
    override val riskTextColor: Int = R.color.high_risk
    override val riskLevel: Int = 100

    override val infectedPeopleMet: Int = infectedPeople
    override val allPeopleMet: Int = totalPeople
    override val circleColorFrom: Int = oldCircleColor
    override val circleColorTo: Int = R.color.high_risk
    override val backgroundIcon: Int = R.drawable.ic_high_risk_background
    override val previousRotation: Int = previousRotation

    override val reportInfectionButtonText: Int = R.string.dashboard_report_infection_end
    override val shouldShowMultilineTitle: Boolean = false

}

class RecoveredState(oldColor: Int,
                     oldCircleColor: Int,
                     riskColorFrom: Int,
                     totalPeople: Int,
                     infectedPeople: Int,
                     previousRotation: Int,
                     override val riskLevel: Int = 0) : InfectionState() {
    override val colorFrom: Int = oldColor
    override val colorTo: Int = R.color.low_background
    override val textOfRiskBeingInfected: Int = R.string.recovered_state
    override val riskTextFromColor: Int = riskColorFrom
    override val riskTextColor: Int = R.color.low_risk

    override val infectedPeopleMet: Int = infectedPeople
    override val allPeopleMet: Int = totalPeople
    override val circleColorFrom: Int = oldCircleColor
    override val circleColorTo: Int = R.color.low_risk
    override val backgroundIcon: Int = R.drawable.ic_low_risk_background
    override val previousRotation: Int = previousRotation

    override val shouldShowReportButton: Boolean = false
    override val reportInfectionButtonText: Int = R.string.dashboard_report_infection_end
    override val shouldShowMultilineTitle: Boolean = false
}