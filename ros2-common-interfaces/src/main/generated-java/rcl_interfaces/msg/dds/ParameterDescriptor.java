package rcl_interfaces.msg.dds;

import us.ihmc.euclid.interfaces.EpsilonComparable;
import us.ihmc.euclid.interfaces.Settable;

/**
 * This is the message to communicate a parameter's descriptor.
 */
public class ParameterDescriptor implements Settable<ParameterDescriptor>, EpsilonComparable<ParameterDescriptor>
{
   private java.lang.StringBuilder name_;
   /**
    * Enum defined in ParameterType.msg
    */
   private byte type_;

   public ParameterDescriptor()
   {
      name_ = new java.lang.StringBuilder(255);
   }

   public ParameterDescriptor(ParameterDescriptor other)
   {
      set(other);
   }

   public void set(ParameterDescriptor other)
   {
      name_.setLength(0);
      name_.append(other.name_);

      type_ = other.type_;
   }

   public java.lang.String getNameAsString()
   {
      return getName().toString();
   }

   public java.lang.StringBuilder getName()
   {
      return name_;
   }

   public void setName(String name)
   {
      name_.setLength(0);
      name_.append(name);
   }

   /**
    * Enum defined in ParameterType.msg
    */
   public byte getType()
   {
      return type_;
   }

   /**
    * Enum defined in ParameterType.msg
    */
   public void setType(byte type)
   {
      type_ = type;
   }

   @Override
   public boolean epsilonEquals(ParameterDescriptor other, double epsilon)
   {
      if (other == null)
         return false;
      if (other == this)
         return true;

      if (!us.ihmc.idl.IDLTools.epsilonEqualsStringBuilder(this.name_, other.name_, epsilon))
         return false;

      if (!us.ihmc.idl.IDLTools.epsilonEqualsPrimitive(this.type_, other.type_, epsilon))
         return false;

      return true;
   }

   @Override
   public boolean equals(Object other)
   {
      if (other == null)
         return false;
      if (other == this)
         return true;
      if (!(other instanceof ParameterDescriptor))
         return false;

      ParameterDescriptor otherMyClass = (ParameterDescriptor) other;

      if (!us.ihmc.idl.IDLTools.equals(this.name_, otherMyClass.name_))
         return false;

      if (this.type_ != otherMyClass.type_)
         return false;

      return true;
   }

   @Override
   public java.lang.String toString()
   {
      StringBuilder builder = new StringBuilder();

      builder.append("ParameterDescriptor {");
      builder.append("name=");
      builder.append(this.name_);

      builder.append(", ");
      builder.append("type=");
      builder.append(this.type_);

      builder.append("}");
      return builder.toString();
   }
}