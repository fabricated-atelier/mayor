package io.fabricatedatelier.mayor.state;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CitizenData {

    private List<UUID> citizens = new ArrayList<>();
    @Nullable
    private BlockPos deskPos = null;
    private int taxAmount;
    private int taxInterval;
    private long taxTime;
    private int registrationFee;
    private List<UUID> taxPayedCitizens = new ArrayList<>();
    private List<UUID> requestCitizens = new ArrayList<>();

    public CitizenData(List<UUID> citizens, @Nullable BlockPos deskPos, int taxAmount, int taxInterval,long taxTime, int registrationFee, List<UUID> taxPayedCitizens, List<UUID> requestCitizens) {
        this.citizens = citizens;
        this.deskPos = deskPos;
        this.taxAmount = taxAmount;
        this.taxInterval = taxInterval;
        this.taxTime = taxTime;
        this.registrationFee = registrationFee;
        this.taxPayedCitizens = taxPayedCitizens;
        this.requestCitizens = requestCitizens;
    }

    public CitizenData(NbtCompound nbt) {
        this.citizens.clear();
        for (int i = 0; i < nbt.getInt("CitizenUuids"); i++) {
            this.citizens.add(nbt.getUuid("CitizenUuid" + i));
        }
        this.deskPos = NbtHelper.toBlockPos(nbt, "DeskPos").orElse(null);
        this.taxAmount = nbt.getInt("TaxAmount");
        this.taxInterval = nbt.getInt("TaxInterval");
        this.taxTime = nbt.getLong("TaxTime");
        this.registrationFee = nbt.getInt("RegistrationFee");
        this.taxPayedCitizens.clear();
        for (int i = 0; i < nbt.getInt("TaxPayedCitizenUuids"); i++) {
            this.taxPayedCitizens.add(nbt.getUuid("TaxPayedCitizenUuid" + i));
        }
        this.requestCitizens.clear();
        for (int i = 0; i < nbt.getInt("RequestCitizenUuids"); i++) {
            this.requestCitizens.add(nbt.getUuid("RequestCitizenUuid" + i));
        }
    }

    public void writeDataToNbt(NbtCompound nbt) {
        nbt.putInt("CitizenUuids", this.citizens.size());
        for (int i = 0; i < this.citizens.size(); i++) {
            nbt.putUuid("CitizenUuid" + i, this.citizens.get(i));
        }
        if (this.deskPos != null) {
            nbt.put("DeskPos", NbtHelper.fromBlockPos(this.deskPos));
        }
        nbt.putInt("TaxAmount", this.taxAmount);
        nbt.putInt("TaxInterval", this.taxInterval);
        nbt.putLong("TaxTime", this.taxTime);
        nbt.putInt("RegistrationFee", this.registrationFee);
        nbt.putInt("TaxPayedCitizenUuids", this.taxPayedCitizens.size());
        for (int i = 0; i < this.taxPayedCitizens.size(); i++) {
            nbt.putUuid("TaxPayedCitizenUuid" + i, this.taxPayedCitizens.get(i));
        }
        nbt.putInt("RequestCitizenUuids", this.requestCitizens.size());
        for (int i = 0; i < this.requestCitizens.size(); i++) {
            nbt.putUuid("RequestCitizenUuid" + i, this.requestCitizens.get(i));
        }
    }

    public NbtCompound writeDataToNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("CitizenUuids", this.citizens.size());
        for (int i = 0; i < this.citizens.size(); i++) {
            nbt.putUuid("CitizenUuid" + i, this.citizens.get(i));
        }
        if (this.deskPos != null) {
            nbt.put("DeskPos", NbtHelper.fromBlockPos(this.deskPos));
        }
        nbt.putInt("TaxAmount", this.taxAmount);
        nbt.putLong("TaxTime", this.taxTime);
        nbt.putInt("RegistrationFee", this.registrationFee);
        nbt.putInt("TaxPayedCitizenUuids", this.taxPayedCitizens.size());
        for (int i = 0; i < this.taxPayedCitizens.size(); i++) {
            nbt.putUuid("TaxPayedCitizenUuid" + i, this.taxPayedCitizens.get(i));
        }
        nbt.putInt("RequestCitizenUuids", this.requestCitizens.size());
        for (int i = 0; i < this.requestCitizens.size(); i++) {
            nbt.putUuid("RequestCitizenUuid" + i, this.requestCitizens.get(i));
        }
        return nbt;
    }

    // Citizens
    public List<UUID> getCitizens() {
        return citizens;
    }

    public void setCitizens(List<UUID> citizens) {
        this.citizens = citizens;
    }

    public void addCitizen(UUID citizen) {
        if (!this.citizens.contains(citizen)) {
            this.citizens.add(citizen);
        }
        if (this.requestCitizens.contains(citizen)) {
            this.requestCitizens.remove(citizen);
        }
    }

    // Don't forget to remove mayor if citizen is mayor
    public void removeCitizen(UUID citizen) {
        this.citizens.remove(citizen);
        if (this.taxPayedCitizens.contains(citizen)) {
            this.taxPayedCitizens.remove(citizen);
        }
    }

    // Desk Pos
    @Nullable
    public BlockPos getDeskPos() {
        return deskPos;
    }

    public void setDeskPos(@Nullable BlockPos deskPos) {
        this.deskPos = deskPos;
    }

    // Tax Amount
    public int getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(int taxAmount) {
        this.taxAmount = taxAmount;
    }

    // Tax Interval
    public int getTaxInterval() {
        return taxInterval;
    }

    public void setTaxInterval(int taxInterval) {
        this.taxInterval = taxInterval;
    }

    // Tax Time
    public long getTaxTime() {
        return taxTime;
    }

    public void setTaxTime(long taxTime) {
        this.taxTime = taxTime;
    }

    // Registration Fee
    public int getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(int registrationFee) {
        this.registrationFee = registrationFee;
    }

    // Tax Payed
    public List<UUID> getTaxPayedCitizens() {
        return taxPayedCitizens;
    }

    public void setTaxPayedCitizens(List<UUID> taxPayedCitizens) {
        this.taxPayedCitizens = taxPayedCitizens;
    }

    public void addTaxPayedCitizen(UUID citizen) {
        if (!this.taxPayedCitizens.contains(citizen)) {
            this.taxPayedCitizens.add(citizen);
        }
    }

    public void removeTaxPayedCitizen(UUID citizen) {
        this.taxPayedCitizens.remove(citizen);
    }

    // Requests
    public List<UUID> getRequestCitizens() {
        return requestCitizens;
    }

    public void setRequestCitizens(List<UUID> requestCitizens) {
        this.requestCitizens = requestCitizens;
    }

    public void addRequestCitizen(UUID citizen) {
        if (!this.requestCitizens.contains(citizen)) {
            this.requestCitizens.add(citizen);
        }
    }

    public void removeRequestCitizen(UUID citizen) {
        this.requestCitizens.remove(citizen);
    }
}
